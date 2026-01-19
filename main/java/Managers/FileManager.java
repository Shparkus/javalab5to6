package Managers;
import ConsoleOperations.Printable;
import Exceptions.FileAccessException;
import Models.Coordinates;
import Models.Location;
import Models.Route;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Класс для чтения/записи XML-файла
public class FileManager {
    private final Printable console;
    private final String filePath;

    public FileManager(Printable console, String filePath) {
        this.console = console;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public HashSet<Route> readCollection() throws FileAccessException {
        if (filePath == null || filePath.trim().isEmpty()) {
            return new HashSet<>();
        }

        File f = new File(filePath);
        if (!f.exists()) {
            return new HashSet<>();
        }
        if (!f.isFile()) {
            throw new FileAccessException("Указанный путь не является файлом: " + filePath, null);
        }
        if (!f.canRead()) {
            throw new FileAccessException("Нет прав на чтение файла: " + filePath, null);
        }

        // Читаем содержимое файла как текст через Scanner
        String xml;
        try (Scanner sc = new Scanner(new FileInputStream(f), StandardCharsets.UTF_8)) {
            xml = "";
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                xml = xml + line + "\n";  // просто конкатенация строк
            }
        } catch (IOException e) {
            throw new FileAccessException("Ошибка при чтении файла: " + filePath, e);
        }

        if (xml.trim().isEmpty()) {
            return new HashSet<>();
        }

        try {
            // Создаём парсер
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new InputSource(new StringReader(xml)));
            Element root = doc.getDocumentElement();

            if (root == null || !"routes".equals(root.getTagName())) {
                throw new FileAccessException("Неверный корневой элемент XML. Ожидался <routes>.", null);
            }

            NodeList nodes = root.getElementsByTagName("route");
            HashSet<Route> result = new HashSet<>();
            HashSet<Integer> seenIds = new HashSet<>();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (n.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element routeEl = (Element) n;
                Route route = parseRoute(routeEl);

                if (route == null) {
                    continue;
                }

                if (!route.validate()) {
                    console.printErr("Пропуск элемента: Route не прошёл валидацию (id=" + route.getId() + ")");
                    continue;
                }
                if (seenIds.contains(route.getId())) {
                    console.printErr("Пропуск элемента: дублирующийся id=" + route.getId());
                    continue;
                }
                seenIds.add(route.getId());
                result.add(route);
            }

            return result;
        } catch (FileAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new FileAccessException("Ошибка разбора XML: " + e.getMessage(), e);
        }
    }

    public void writeCollection(Collection<Route> routes) throws FileAccessException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new FileAccessException("Переменная окружения с именем файла не задана.", null);
        }

        File f = new File(filePath);
        if (f.exists() && !f.canWrite()) {
            throw new FileAccessException("Нет прав на запись файла: " + filePath, null);
        }

        List<Route> sorted = new ArrayList<Route>(routes);
        Collections.sort(sorted, new Comparator<Route>() {
            @Override
            public int compare(Route r1, Route r2) {
                return Integer.compare(r1.getId(), r2.getId());
            }
        });

        String xml = serialize(sorted);

        try (FileOutputStream fos = new FileOutputStream(f, false)) {
            byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            throw new FileAccessException("Ошибка при записи файла: " + filePath, e);
        }
    }

    private Route parseRoute(Element routeEl) {
        try {
            Integer id = parseInt(getText(routeEl, "id"));
            String name = getText(routeEl, "name");
            Long creationMillis = parseLong(getText(routeEl, "creationDate"));
            Long distance = parseLong(getText(routeEl, "distance"));

            Element coordsEl = getFirstChildElement(routeEl, "coordinates");
            Element fromEl = getFirstChildElement(routeEl, "from");
            Element toEl = getFirstChildElement(routeEl, "to"); // может отсутствовать

            if (id == null || name == null || creationMillis == null
                    || distance == null || coordsEl == null || fromEl == null) {
                console.printErr("Пропуск Route: нет обязательных полей.");
                return null;
            }

            // coordinates
            Float cx = parseFloat(getText(coordsEl, "x"));
            Long cy = parseLong(getText(coordsEl, "y"));
            if (cx == null || cy == null) {
                console.printErr("Пропуск Route: некорректные coordinates.");
                return null;
            }
            Coordinates coords = new Coordinates(cx, cy);

            // from (обязательное поле)
            Location from = parseLocation(fromEl);
            if (from == null) {
                console.printErr("Пропуск Route: некорректное from.");
                return null;
            }

            // to (может отсутствовать или быть null)
            Location to = null;
            if (toEl != null) {
                to = parseLocation(toEl);
                if (to == null) {
                    console.printErr("Пропуск Route: некорректное to.");
                    return null;
                }
            }

            Route r = new Route();
            r.setId(id);
            r.setName(name);
            r.setCoordinates(coords);
            r.setFrom(from);
            r.setTo(to);
            r.setDistance(distance);
            r.setCreationDate(new Date(creationMillis));
            return r;
        } catch (Exception e) {
            console.printErr("Пропуск Route: ошибка парсинга: " + e.getMessage());
            return null;
        }
    }

    private Location parseLocation(Element el) {
        Double x = parseDouble(getText(el, "x"));
        Integer y = parseInt(getText(el, "y"));
        Float z = parseFloat(getText(el, "z"));
        String name = emptyToNull(getTextOptional(el, "name"));

        if (x == null || y == null || z == null) {
            return null;
        }
        return new Location(x, y, z, name);
    }

    // Строим XML-строку из списка Route
    private String serialize(List<Route> routes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<routes>\n");

        for (Route r : routes) {
            sb.append("  <route>\n");

            sb.append("    <id>").append(r.getId()).append("</id>\n");
            sb.append("    <name>").append(escapeXml(r.getName())).append("</name>\n");

            // coordinates
            sb.append("    <coordinates>\n");
            if (r.getCoordinates() != null) {
                sb.append("      <x>").append(r.getCoordinates().getX()).append("</x>\n");
                sb.append("      <y>").append(r.getCoordinates().getY()).append("</y>\n");
            } else {
                sb.append("      <x>0</x>\n");
                sb.append("      <y>0</y>\n");
            }
            sb.append("    </coordinates>\n");

            // creationDate
            long millis = (r.getCreationDate() != null)
                    ? r.getCreationDate().getTime()
                    : System.currentTimeMillis();
            sb.append("    <creationDate>").append(millis).append("</creationDate>\n");

            // from
            sb.append("    <from>\n");
            Location from = r.getFrom();
            if (from != null) {
                sb.append("      <x>").append(from.getX()).append("</x>\n");
                sb.append("      <y>").append(from.getY()).append("</y>\n");
                sb.append("      <z>").append(from.getZ()).append("</z>\n");
                sb.append("      <name>")
                        .append(from.getName() == null ? "" : escapeXml(from.getName()))
                        .append("</name>\n");
            } else {
                sb.append("      <x>0.0</x>\n");
                sb.append("      <y>0</y>\n");
                sb.append("      <z>0.0</z>\n");
                sb.append("      <name></name>\n");
            }
            sb.append("    </from>\n");

            // to (может быть null)
            Location to = r.getTo();
            if (to != null) {
                sb.append("    <to>\n");
                sb.append("      <x>").append(to.getX()).append("</x>\n");
                sb.append("      <y>").append(to.getY()).append("</y>\n");
                sb.append("      <z>").append(to.getZ()).append("</z>\n");
                sb.append("      <name>")
                        .append(to.getName() == null ? "" : escapeXml(to.getName()))
                        .append("</name>\n");
                sb.append("    </to>\n");
            }

            sb.append("    <distance>").append(r.getDistance()).append("</distance>\n");

            sb.append("  </route>\n");
        }

        sb.append("</routes>\n");
        return sb.toString();
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static Element getFirstChildElement(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0) return null;
        Node n = list.item(0);
        return (n instanceof Element) ? (Element) n : null;
    }

    private static String getText(Element parent, String tag) {
        Element el = getFirstChildElement(parent, tag);
        if (el == null) return null;
        return el.getTextContent();
    }

    private static String getTextOptional(Element parent, String tag) {
        return getText(parent, tag);
    }

    private static Integer parseInt(String s) {
        if (s == null) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long parseLong(String s) {
        if (s == null) return null;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Float parseFloat(String s) {
        if (s == null) return null;
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double parseDouble(String s) {
        if (s == null) return null;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
