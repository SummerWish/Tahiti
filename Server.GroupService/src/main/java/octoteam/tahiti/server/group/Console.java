package octoteam.tahiti.server.group;

import octoteam.tahiti.server.group.service.DefaultGroupService;
import octoteam.tahiti.server.shared.microservice.rmi.IGroupServiceProvider;
import octoteam.tahiti.server.shared.microservice.rmi.RegistryConfig;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Console {

    public static void main(String[] args) throws Exception {
        IGroupServiceProvider groupService = new DefaultGroupService();
        try {
            LocateRegistry.createRegistry(RegistryConfig.GroupServiceBindPort);
            Naming.rebind(
                    "rmi://" + RegistryConfig.GroupServiceBindIp + ":" + RegistryConfig.GroupServiceBindPort + "/GroupService",
                    groupService
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
