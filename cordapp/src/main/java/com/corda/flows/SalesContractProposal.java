package com.corda.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.corda.states.SalesContract;
import com.corda.states.SalesContractState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import jdk.nashorn.internal.parser.JSONParser;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import javax.json.JsonString;
import java.security.PublicKey;
import java.util.List;

public class SalesContractProposal {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final Party buyer;
        private final String contractData;

        private final ProgressTracker.Step INITIALISING = new ProgressTracker.Step("Performing initial steps.");
        private final ProgressTracker.Step BUILDING = new ProgressTracker.Step("Performing initial steps.");
        private final ProgressTracker.Step SIGNING = new ProgressTracker.Step("Signing transaction.");
        private final ProgressTracker.Step COLLECTING = new ProgressTracker.Step("Collecting counterparty signature.") {
            @Override public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.Companion.tracker();
            }
        };

        private final ProgressTracker.Step FINALISING = new ProgressTracker.Step("Finalising transaction.") {
            @Override public ProgressTracker childProgressTracker() {
                return FinalityFlow.Companion.tracker();
            }
        };

        private final ProgressTracker progressTracker = new ProgressTracker(
                INITIALISING, BUILDING, SIGNING, COLLECTING, FINALISING
        );

        public Initiator(Party buyer, String contractData) {
            this.buyer = buyer;
            this.contractData = contractData;
        }
        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            progressTracker.setCurrentStep(INITIALISING);
            final SalesContractState outputSCProposalState = new SalesContractState(getOurIdentity(),buyer,contractData);
            final List<PublicKey> requiredSignerKeys = outputSCProposalState.getParticipantKeys();
            if(requiredSignerKeys.size() !=2)
            {
                throw new IllegalStateException("Participant keys are not present");
            }
            progressTracker.setCurrentStep(BUILDING);
            List<Party> notaries = getServiceHub().getNetworkMapCache().getNotaryIdentities();
            if (notaries.isEmpty()) {
                throw new FlowException("No available notary.");
            }
            final TransactionBuilder unsignedTransaction = new TransactionBuilder(notaries.get(0))
                    .addOutputState(outputSCProposalState,SalesContract.SALES_CONTRACT_ID)
                    .addCommand(new SalesContract.Commands.ProposeLC(),requiredSignerKeys);
            progressTracker.setCurrentStep(SIGNING);
            final SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(unsignedTransaction, getOurIdentity().getOwningKey());

            progressTracker.setCurrentStep(COLLECTING);

            final FlowSession buyerFlow = initiateFlow(buyer);
            final SignedTransaction stx = subFlow(new CollectSignaturesFlow(
                    signedTransaction,
                    ImmutableSet.of(buyerFlow),
                    ImmutableList.of(getOurIdentity().getOwningKey()),
                    COLLECTING.childProgressTracker())
            );
            // Step 5. Finalise the transaction.
            progressTracker.setCurrentStep(FINALISING);

            return subFlow(new FinalityFlow(stx,FINALISING.childProgressTracker()));
        }
    }

    @InitiatedBy(SalesContractProposal.Initiator.class)
    public static class Responder extends FlowLogic<SignedTransaction> {
        private final FlowSession otherFlow;

        public Responder(FlowSession otherFlow) {
            this.otherFlow = otherFlow;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            return subFlow(new SignTransactionFlow(otherFlow,SignTransactionFlow.Companion.tracker()) {
                @Override
                protected void checkTransaction(SignedTransaction stx) throws FlowException {
                    // no checking here
                }
            });
        }
    }
}
