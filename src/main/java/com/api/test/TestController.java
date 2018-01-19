package com.api.test;

import com.api.config.NodeRpcConnection;
import net.corda.core.identity.Party;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/test")
public class TestController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final NodeRpcConnection rpc;

    public TestController(NodeRpcConnection rpc){
        this.rpc = rpc;
    }
    @RequestMapping(method= RequestMethod.GET)
    public @ResponseBody
    Test sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
        return new Test(counter.incrementAndGet(), String.format(template, name));
    }

    @RequestMapping(value="/node", method= RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> getNodeInfo(@RequestParam(value="name", required=false, defaultValue="Node") String name) {
        System.out.println("requesting node information");
        try{
             final Party party = rpc.getProxy().nodeInfo().getLegalIdentities().get(0);
             return ResponseEntity.ok().body("Party Details:"+ party.toString() );
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage() );
        }

    }
}
