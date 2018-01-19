package com.api.config;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;


@Configuration
@PropertySource("classpath:application.properties")
public class NodeRpcConnection {
    private static final String CORDA_USER_NAME = "config.rpc.username";
    private static final String CORDA_USER_PASSWORD = "config.rpc.password";
    private static final String CORDA_NODE_HOST = "config.rpc.host";
    private static final String CORDA_RPC_PORT = "config.rpc.port";

    @Value("${config.rpc.host}")
    private String host;

    @Value("${config.rpc.username}")
    private String userName;

    @Value("${config.rpc.password}")
    private String password;

    @Value("${config.rpc.port}")
    private Integer rpcPort;

    private static CordaRPCOps proxy;

    @PostConstruct
    public void init() {
        System.out.println("Initializing proxy");
        try {

            final NetworkHostAndPort nodeAddress = new NetworkHostAndPort(host, rpcPort);
            final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);
            proxy = client.start(userName, password).getProxy();
            System.out.println("Proxy initialized successfully");
        } catch (Exception e) {
            System.out.println("Unable to initialize proxy:");
            System.out.println(e.getMessage());
        }

    }

    public CordaRPCOps getProxy() {
        return proxy;
    }

    @Override
    public String toString() {
        return "NodeRpcConnection{" +
                "host='" + host + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", rpcPort=" + rpcPort +
                '}';
    }
}
