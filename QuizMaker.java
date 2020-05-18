

public class QuizMaker {
    public QuizMaker() {
    }

    public static void main(String[] var0) throws Exception {
        Quiz var1 = Quiz.loadFromFile(var0[0]);
        if (var1 != null) {
            var1.start();
        }

    }
}
