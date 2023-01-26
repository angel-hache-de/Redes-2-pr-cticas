import dao.ProductDAOImpl;
import model.Product;
import model.User;
import views.Login;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception{
        System.out.println("Hello world!");
//        String url = "jdbc:mysql://localhost:3306/song_store";
//        String uname = "demo_java";
//
//        String pass = "1234";
//        Class.forName("com.mysql.cj.jdbc.Driver");
//
//        Connection con = DriverManager.getConnection(url, uname, pass);
//        Statement st = con.createStatement();
//        String query = "select * from user";
//        ResultSet rs = st.executeQuery(query);
//
//        while(rs.next()) {
//            System.out.println(
//                    rs.getString(2)
//            );
//        }
        
//        st.close();
//        con.close();

//        List<String> l = List.of("Algo", "Algo2", "Algo3");
//        ArrayList<String> l = new ArrayList<String>();
//        l.add("1");
//        l.add("2");
//        l.add("3");
//        byte[] byteArray = SerializationUtils.serialize((Serializable) l);
//        System.out.println("Length: " + byteArray.length);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        byte[] byteArray;
//        ObjectOutputStream oos;
//        try {
//            oos = new ObjectOutputStream(baos);
//            oos.writeObject(l);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // Convert to Byte Array
//        byteArray = baos.toByteArray();


//        List<String> l2 = (List<String>) SerializationUtils.deserialize(byteArray);
//        l2.stream().forEach(
//                System.out::println
//        );

//        String cadena = "Ella si se lleva bien con mis amigos nunca discutimos";
//        byte[] arreglo = cadena.getBytes();
//
//
//        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(arreglo));
//
//        int bytesToRead = arreglo.length;
//        int packageSize = 10;
//        while(bytesToRead > 0) {
//            byte[] leido = new byte[bytesToRead >= packageSize ? packageSize : bytesToRead];
//            bytesToRead -=  dis.read(leido);
//
//            System.out.println("Len is " + leido.length);
//            System.out.println(new String(leido));
//        }

//        HashMap<Integer, Integer> hm = new HashMap<>();
//        hm.put(0, 10);
//        hm.put(1, 20);
//        hm.put(3, 30);

//        hm.entrySet().stream().map(Map.Entry::getValue).forEach(System.out::println);
//        hm.entrySet().stream().map(Map.Entry::getKey).forEach(System.out::println);
//         Login login = new Login();
//        User user = null;
//        Optional<User> user1 = getUser(user);
//        User user1 = Optional.ofNullable(new User(user.getId())).orElseThrow();
//        if(user1.isEmpty()) System.out.println("IS EMPTY");
//        else System.out.println(user1.get().toString());
//        System.out.println();

//        List<String> list1 = List.of("Clavado en un bar", "21.10", "5", "Bar", "Maná", "2017", "3:01", "clavado en un bar.mp3", "mana.jpeg");
//        List<String> list2 = List.of("Corazón de acero", "13.14", "2", "Corazón de acero", "Yiyo Sarante", "2004", "3:30", "corazon de acero.mp3", "cda.jpeg");
//        List<String> list3 = List.of("El ataque de las chichas cocodrilo", "10.00", "10", "El ataque", "Hombres G", "2001", "3:42", "el ataque de las chicas cocodrilo.mp3", "cocodrilo.jpeg");
//        List<String> list4 = List.of("Oye mi amor", "11.40", "9", "Oye", "Maná", "2004", "4:00", "oye mi amor.mp3", "mana.jpeg");
//        List<String> list5 = List.of("Rosa pastel", "8.80", "12", "Rosa", "Belanova", "2003", "3:45", "rosa pastel.mp3", "rosa.jpeg");
//        List<String> list6 = List.of("No te contaron mal", "5.20", "32", "Mal", "Nodal", "2013", "3:04", "NO TE CONTARON MAL.mp3", "mal.jpeg");
//        List<String> list7 = List.of("A través del vaso", "12.40", "21", "Vaso", "Los Sebastianes", "2018", "3:28", "A TRAVÉS DEL VASO.mp3", "vaso.jpeg");
//        List<String> list8 = List.of("Sensualidad", "12.20", "49", "Sensualidad", "Bad bunny", "2018", "3:42", "sensualidad.mp3", "sensu.jpeg");
//        List<String> list9 = List.of("Si tu amor no vuelve", "10.00", "50", "Amor", "La arrolladora", "2011", "3:34", "SI TU AMOR NO VUELVE.mp3", "vuelve.jpeg");
//        List<String> list10 = List.of("The 7th element", "30.10", "100", "Vitas", "Vitas", "2000", "3:24", "element.mp3", "element.jpeg");
//
//        List<List<String>> list = List.of(list1, list2, list3, list4, list5, list6, list7, list8, list9, list10);
//
//        ProductDAOImpl productDAO = new ProductDAOImpl();
//        for (List<String> strings : list) {
//            String name = strings.get(0);
//            float price = Float.parseFloat(strings.get(1));
//            int downloads = Integer.parseInt(strings.get(2));
//            String album = strings.get(3);
//            String artist = strings.get(4);
//            int year = Integer.parseInt(strings.get(5));
//            String duration = strings.get(6);
//            String aduifileName = strings.get(7);
//            String fileName = strings.get(8);
//
//            Product p = new Product(
//                name, album, year, price, duration, artist, downloads, null
//            );
//
//            productDAO.insertProduct(p, fileName, aduifileName);
//        }


        Login login = new Login();
        login.setVisible(true);

//        File outputFile = tempFolder.newFile("outputFile.jpg");
//        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
//            outputStream.write(dataForWriting);
//        }

//        LineItem lineItem = new LineItem(
//            new Product(3, "Bored to death1", "California", 2017, (float) 21.1, "3:01", "Blink-182", 10, new ImageIcon("/home/angel-hache/Pictures/Wallpapers/ubuntu-2.png")),
//            12
//        );
//        LineItem lineItem1 = new LineItem(
//                new Product(3, "Bored to death2", "California", 2017, (float) 21.1, "3:01", "Blink-182", 10, new ImageIcon("/home/angel-hache/Pictures/Wallpapers/ubuntu-2.png")),
//                12
//        );
//        LineItem lineItem2 = new LineItem(
//                new Product(3, "Bored to death3", "California", 2017, (float) 21.1, "3:01", "Blink-182", 10, new ImageIcon("/home/angel-hache/Pictures/Wallpapers/ubuntu-2.png")),
//                12
//        );
//        LineItem lineItem3 = new LineItem(
//                new Product(3, "Bored to death4", "California", 2017, (float) 21.1, "3:01", "Blink-182", 10, new ImageIcon("/home/angel-hache/Pictures/Wallpapers/ubuntu-2.png")),
//                12
//        );
//
//        List<LineItem> lineItem4 = List.of(lineItem, lineItem1, lineItem2, lineItem3);
//        System.out.println("Index: " + lineItem4.indexOf(lineItem2));
    }

    public static Optional<User> getUser(User u) {
        if(u == null) return Optional.empty();
        else return Optional.of(u);
    }


}