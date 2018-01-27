package com.api.controllers;

import com.api.config.NodeRpcConnection;
import com.corda.states.SalesContractState;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.corda.core.contracts.StateAndRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salescontract")
@Api(value="Sales contract", description="Sales contract operstion endpoints")
public class SalesContractController {
    @Autowired
    private NodeRpcConnection rpcOps;

    @GetMapping
    @ApiOperation(value = "Get all Sales Contracts",response = SalesContractState.class)
    public @ResponseBody
    List<SalesContractState> salesContracts() {
        List<StateAndRef<SalesContractState>> scStateRefList = rpcOps.getProxy().vaultQuery(SalesContractState.class).getStates();
       // TransactionState<SalesContractState> SCTransactionState = scStateRefList.get(0).component1();

        List<SalesContractState> collect = scStateRefList.stream().map(StateAndRef -> StateAndRef.component1().getData()).collect(Collectors.toList());
        System.out.println(collect);

       // System.out.println(rpcOps.getProxy().vaultQuery(SalesContractState.class).getStates());
        return collect;
    }
}
