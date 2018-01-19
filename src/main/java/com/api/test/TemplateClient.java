package com.api.test;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Demonstration of how to use the CordaRPCClient to connect to a Corda Node and
 * stream the contents of the node's vault.
 */
public class TemplateClient {
    private static final Logger logger = LoggerFactory.getLogger(TemplateClient.class);
    public static void main(String[] args)  {

        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse("localhost:10009");
        final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);

        // Can be amended in the Main file.
        final CordaRPCOps proxy = client.start("user1", "test").getProxy();

        // Grab all existing TemplateStates and all future TemplateStates.
        Party party  = proxy.nodeInfo().getLegalIdentities().get(0);
        System.out.println(party);
    }
}