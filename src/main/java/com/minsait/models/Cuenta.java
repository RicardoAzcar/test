package com.minsait.models;

import com.minsait.exceptions.DineroInsuficienteException;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class Cuenta {
    @NonNull
    private String persona;
    @NonNull
    private BigDecimal saldo;
    private Banco banco;

    public void retirar(BigDecimal monto){
        BigDecimal saldoAux = this.saldo.subtract(monto);
        if (saldoAux.compareTo(BigDecimal.ZERO)<0){
            throw new DineroInsuficienteException("Dinero Insuficiente");
        }
        this.saldo = saldoAux;
    }

    public void depositar(BigDecimal monto){
        this.saldo=this.saldo.add(monto);
    }
}
