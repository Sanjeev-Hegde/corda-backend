package com.api.test;

import com.api.config.NodeRpcConnection;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.corda.core.identity.Party;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/test")
@Api(value="test", description="Some sample test endpoints")
public class TestController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    private final NodeRpcConnection rpc;

    public TestController(NodeRpcConnection rpc){
        this.rpc = rpc;
    }

    @ApiOperation(value = "Sample Test endpoint",response = Test.class)
    @RequestMapping(method= RequestMethod.GET)
    public @ResponseBody
    Test sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
        return new Test(counter.incrementAndGet(), String.format(template, name));
    }

    @ApiOperation(value = "Gives node information")
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
