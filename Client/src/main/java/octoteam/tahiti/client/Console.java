package octoteam.tahiti.client;

import octoteam.tahiti.client.configuration.ClientConfiguration;
import octoteam.tahiti.client.ui.Reactor;
import octoteam.tahiti.client.ui.Renderer;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class Console {

    public static void main(String[] args) throws Exception {

        Yaml yaml = new Yaml();
        InputStream in = Console.class.getClass().getResourceAsStream("/tahiti_client.yaml");
        ClientConfiguration config = yaml.loadAs(in, ClientConfiguration.class);

        TahitiClient client = new TahitiClient(config);
        Renderer renderer = new Renderer();

        Reactor reactor = new Reactor(client, renderer);
        client.getEventBus().register(reactor);
        renderer.getEventBus().register(reactor);

        renderer.actionShowLoginDialog();

/*


        client.connectAsync();

        ConsoleReader reader = new ConsoleReader();
        String line = null;
        do {
            line = reader.readLine("Tahiti>");
            if (line != null) {
                if (line.equals("shutdown")) {
                    System.out.println("Ready to shutdown");
                    client.shutdown();
                }
            }
        } while (line != null);
*/
/*
        ConsoleReader reader = new ConsoleReader();
        String line = null;
        do {
            line = reader.readLine("Tahiti>");
            if (line != null) {
                if (line.equals("login")) {
                    String username = reader.readLine("Username: ");
                    String password = reader.readLine("Password: ", '*');
                    SocketMessageProtos.Message.Builder req = SocketMessageProtos.Message
                            .newBuilder()
                            .setSeqId(msgSequence.getAndIncrement())
                            .setDirection(SocketMessageProtos.Message.DirectionCode.REQUEST)
                            .setService(SocketMessageProtos.Message.ServiceCode.USER_SIGN_IN_REQUEST)
                            .setUserSignInReq(SocketMessageProtos.UserSignInReqBody.newBuilder()
                                    .setUsername(username)
                                    .setPassword(password)
                            );
                    ch.writeAndFlush(req.build());
                } else if (line.equals("ping")) {
                    String payload = reader.readLine("Payload: ");
                    SocketMessageProtos.Message.Builder req = SocketMessageProtos.Message
                            .newBuilder()
                            .setSeqId(msgSequence.getAndIncrement())
                            .setDirection(SocketMessageProtos.Message.DirectionCode.REQUEST)
                            .setService(SocketMessageProtos.Message.ServiceCode.PING_REQUEST)
                            .setPingPong(SocketMessageProtos.PingPongBody.newBuilder()
                                    .setPayload(payload)
                            );
                    ch.writeAndFlush(req.build());
                } else if (line.equals("send")) {
                    throw new NotImplementedException();
                }
            }
        }
        while (line != null);
        */

    }

}
