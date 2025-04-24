package outros;

public class Utils {
    public static void limparConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
