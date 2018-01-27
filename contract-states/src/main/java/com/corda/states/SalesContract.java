package com.corda.states;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class SalesContract implements Contract {
    public static final String SALES_CONTRACT_ID = "com.corda.states.SalesContract";

    public interface Commands extends CommandData {
        class ProposeSC extends TypeOnlyCommandData implements Commands {
        }

        class AcceptSC extends TypeOnlyCommandData implements Commands {
        }

        class ProposeLC extends TypeOnlyCommandData implements Commands {
        }
    }

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final Commands commandData = command.getValue();
        final Set<PublicKey> setOfSigners = new HashSet<>(command.getSigners());
        if (commandData instanceof Commands.ProposeSC) {
            verifyProposeSC(tx, setOfSigners);
        } else if (commandData instanceof Commands.AcceptSC) {
            verifyAcceptSC(tx, setOfSigners);
        } else if (commandData instanceof Commands.ProposeLC) {
            verifyProposeLC(tx, setOfSigners);
        } else {
            throw new IllegalArgumentException("Unrecognised command.");
        }
    }


    private void verifyProposeSC(LedgerTransaction tx, Set<PublicKey> signers) {
        requireThat(req -> {
            req.using("No inputs should be consumed when issuing an obligation.",
                    tx.getInputStates().isEmpty());
            req.using("Only one Sales Contract state should be created when proposing SC", tx.getOutputStates().size() == 1);
            SalesContractState salesContractProposal = (SalesContractState) tx.getOutputStates().get(0);
            req.using("The buyer and seller cannot be the same identity.", !salesContractProposal.getBuyer().equals(salesContractProposal.getSeller()));
            req.using("Both buyer and seller together only may sign Sales contract Proposal",
                    signers.contains(salesContractProposal.getParticipants()));
            return null;
        });
    }

    private void verifyAcceptSC(LedgerTransaction tx, Set<PublicKey> signers) {
        requireThat(req -> {
            return null;
        });
    }


    private void verifyProposeLC(LedgerTransaction tx, Set<PublicKey> signers) {
        requireThat(req -> {
            return null;
        });
    }


}
