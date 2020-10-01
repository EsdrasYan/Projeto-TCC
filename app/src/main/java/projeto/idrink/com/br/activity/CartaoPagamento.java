package projeto.idrink.com.br.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import projeto.idrink.com.br.R;

import com.braintreepayments.cardform.view.CardForm;

public class CartaoPagamento extends AppCompatActivity {

    CardForm cardForm;
    Button btnComprar;
    AlertDialog.Builder alertBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao_pagamento);

        cardForm = findViewById(R.id.cartaoForm);
        btnComprar = findViewById(R.id.btnComprar);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .setup(CartaoPagamento.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        btnComprar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(cardForm.isValid()){
                    alertBuilder = new AlertDialog.Builder(CartaoPagamento.this);
                    alertBuilder.setTitle("Confirme antes de prosseguir");
                    alertBuilder.setMessage("Titular: " + cardForm.getCardholderName() + "\n" +
                            "Número do Cartão: " + cardForm.getCardNumber() + "\n" +
                            "Vencimento: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "CVV: " + cardForm.getCvv());
                    alertBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(CartaoPagamento.this, "Obrigado pela preferência", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(CartaoPagamento.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                    alertBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                }else{
                    Toast.makeText(CartaoPagamento.this, "Por favor, complete os dados", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}