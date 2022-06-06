package org.example;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MainWindow {

    private JFrame frame;
    private JTextField input;
    private static String ticker;
    private static HttpResponse<String> response;
    private static String name;
    private static double cena;

    private static final String URL = "https://pro-api.coinmarketcap.com/v2/tools/price-conversion?amount=1&symbol=";

    //Formatiranje api response (JSON) v stringe oz. double
    public static void parseData(String responseBody){
        try{
            //Formatiranje podatkov "JSON"
            JSONObject object = new JSONObject(responseBody);
            JSONArray data = object.getJSONArray("data");
            JSONObject dataObject = data.getJSONObject(0);
            name = dataObject.getString("name");
            JSONObject quote = dataObject.getJSONObject("quote");
            JSONObject usd = quote.getJSONObject("USD");
            cena = usd.getDouble("price");

            System.out.println(dataObject.getInt("id"));
        } catch (JSONException e){
            System.out.println("Error: Napačna koda");
        }

    }

    //Klic api in nastavljanje parametrov ter authenitifkacija ključa za dostop do api
    public static void queryApi(String ticker) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("X-CMC_PRO_API_KEY", "8d9b8375-be05-4c35-9aa9-31d540928356")
                .uri(URI.create(URL + ticker.toUpperCase()))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        parseData(response.body());
        System.out.println(URL + ticker.toUpperCase());
    }



    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBackground(Color.GRAY);
        frame.setBounds(100, 100, 292, 282);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblCryptoInput = new JLabel("Vnesite kodo za valuto npr: BTC");
        lblCryptoInput.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblCryptoInput.setBounds(10, 11, 356, 23);
        frame.getContentPane().add(lblCryptoInput);


        input = new JTextField();
        input.setFont(new Font("Tahoma", Font.PLAIN, 28));
        input.setBounds(10, 38, 157, 40);
        frame.getContentPane().add(input);
        input.setColumns(10);

        final JLabel outputName = new JLabel("Ime: ");
        outputName.setFont(new Font("Tahoma", Font.PLAIN, 18));
        outputName.setBounds(10, 89, 256, 41);
        frame.getContentPane().add(outputName);

        final JLabel outputSymbol = new JLabel("Simbol: ");
        outputSymbol.setFont(new Font("Tahoma", Font.PLAIN, 18));
        outputSymbol.setBounds(10, 141, 256, 41);
        frame.getContentPane().add(outputSymbol);

        final JLabel outputPrice = new JLabel("Cena:");
        outputPrice.setFont(new Font("Tahoma", Font.PLAIN, 18));
        outputPrice.setBounds(10, 193, 256, 41);
        frame.getContentPane().add(outputPrice);

        JButton btnSubmit = new JButton("Najdi");
        btnSubmit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ticker = input.getText();
                if(ticker.length()!=0 && ticker.length()<11) {
                    System.out.println(ticker);
                    //Kliči api
                    try {
                        queryApi(ticker);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    //Prikaži podatke v GUI
                    outputName.setText(String.format("Ime: %s", name));
                    outputSymbol.setText(String.format("Simbol: %S", ticker));
                    outputPrice.setText(String.format("Cena: %.2f$", cena));
                }
            }
        });
        btnSubmit.setBounds(177, 37, 89, 41);
        frame.getContentPane().add(btnSubmit);

    }
}
