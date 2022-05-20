package sk.stuba.fei.uim.vsa.pr1;

public class TestData {

    public static final String DB = "VSA_PR1";
    public static final String USERNAME = "vsa";
    public static final String PASSWORD = "vsa";

    public static final String CAR_TYPE_NAME = "benzín";
    public static final String CAR_TYPE_NAME_ALT = "electric";

    public static class User {
        public static String firstName = "Jožko";
        public static String lastName = "Mrkvička";
        public static String email = "jozko.mrkvicka@example.com";
    }

    public static class Car {
        public static String brand = "Audi";
        public static String model = "A7";
        public static String colour = "black";
        public static String ecv = "BL798XX";
    }

    public static class User2 {
        public static String firstName = "Jožko2";
        public static String lastName = "Mrkvička2";
        public static String email = "jozko.mrkvicka2@example.com";
    }

    public static class Car2 {
        public static String brand = "Audi";
        public static String model = "A7";
        public static String colour = "black";
        public static String ecv = "BL799XX";
    }

    public static class User3 {
        public static String firstName = "Jožko3";
        public static String lastName = "Mrkvička3";
        public static String email = "jozko.mrkvicka3@example.com";
    }

    public static class Car3 {
        public static String brand = "Audi";
        public static String model = "A7";
        public static String colour = "black";
        public static String ecv = "BL700XX";
    }

    public static class CarPark {
        public static final String name = "TestCarPark";
        public static final String address = "Ulica 123";
        public static final Integer price = 12;
        public static final String floor = "Floor 1-1";
        public static final String spot = "1.01";
        public static final String spot2 = "1.02";
    }

    public static class Coupon {
        public static final String name = "TestDiscountCoupon";
        public static final Integer discount = 20;
    }

}
