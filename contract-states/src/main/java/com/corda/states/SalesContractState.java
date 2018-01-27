package com.corda.states;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import java.security.PublicKey;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SalesContractState implements LinearState {

    private final AbstractParty seller;
    private final AbstractParty buyer;
    private final String contractData;
    private final UniqueIdentifier linearId;

    public SalesContractState(Party seller, Party buyer, String contractData) {
        this.seller = seller;
        this.buyer = buyer;
        this.contractData = contractData;
        this.linearId = new UniqueIdentifier();
    }

    public AbstractParty getSeller() {
        return seller;
    }

    public AbstractParty getBuyer() {
        return buyer;
    }

    public String getContractData() {
        return contractData;
    }

    @JsonIgnore
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(seller,buyer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesContractState that = (SalesContractState) o;
        return Objects.equals(seller, that.seller) &&
                Objects.equals(buyer, that.buyer) &&
                Objects.equals(contractData, that.contractData) &&
                Objects.equals(linearId, that.linearId);
    }

    @Override
    public String toString() {
        return "SalesContractState{" +
                "seller=" + seller.getOwningKey() +
                ", buyer=" + buyer.getOwningKey()+
                ", contractData='" + contractData + '\'' +
                ", linearId=" + linearId +
                '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(seller, buyer, contractData, linearId);
    }

    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @JsonIgnore
    public List<PublicKey> getParticipantKeys() {
        return getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList());
    }


}
