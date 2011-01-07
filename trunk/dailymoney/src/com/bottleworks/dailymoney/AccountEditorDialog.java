package com.bottleworks.dailymoney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.bottleworks.commons.util.Formats;
import com.bottleworks.commons.util.OnDialogFinishListener;
import com.bottleworks.commons.util.Logger;
import com.bottleworks.dailymoney.data.Account;
import com.bottleworks.dailymoney.data.AccountType;
import com.bottleworks.dailymoney.ui.Contexts;

/**
 * Edit or create a account
 * @author dennis
 *
 */
public class AccountEditorDialog extends Dialog implements android.view.View.OnClickListener{

    
    private boolean modeCreate;
    private Account account;
    private Account workingAccount;
    private OnDialogFinishListener listener;
    
    public AccountEditorDialog(Context context,OnDialogFinishListener listener,boolean modeCreate,Account account) {
        super(context,R.style.theme_acceidtor);
        this.modeCreate = modeCreate;
        this.account = account;
        this.listener = listener;
        workingAccount = new Account(account.getName(),account.getType(),account.getInitialValue());
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_acceditor);
        setContentView(R.layout.acceditor);
        initialEditor();
    }
    
    public Account getAccount(){
        return account;
    }
    
    public Account getWorkingAccount(){
        return workingAccount;
    }
    
    public boolean isModeCreate(){
        return modeCreate;
    }
    
    
    private static String[] spfrom = new String[] { "display"};
    private static int[] spto = new int[] { R.id.simple_spitem_display};
    
    EditText nameEditor;
    EditText initvalEditor;
    Spinner typeEditor; 
    
    private void initialEditor() {
        nameEditor = (EditText)findViewById(R.id.acceditor_name);
        nameEditor.setText(workingAccount.getName());
        
        initvalEditor = (EditText)findViewById(R.id.acceditor_initval);
        initvalEditor.setText(Formats.double2String(workingAccount.getInitialValue()));
        
        //initial spinner
        typeEditor = (Spinner) findViewById(R.id.acceditor_type);
        List<Map<String, Object>> data = new  ArrayList<Map<String, Object>>();
        String type = workingAccount.getType();
        int selpos,i;
        selpos = i = 0;
        for (AccountType at : AccountType.getSupportedType()) {
            Map<String, Object> row = new HashMap<String, Object>();
            data.add(row);
            row.put(spfrom[0], AccountType.getDisplay(Contexts.instance().getI18n(),at.getType()));
            
            if(at.getType().equals(type)){
                selpos = i;
            }
            i++;
        }
        SimpleAdapter adapter = new SimpleAdapter(getContext(), data, R.layout.simple_spitem, spfrom, spto);
        adapter.setDropDownViewResource(R.layout.simple_spdd);
        
        typeEditor.setAdapter(adapter);
        typeEditor.setSelection(selpos);
        typeEditor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                AccountType type = AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()];
                checkType(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        
        
        checkType(AccountType.getSupportedType()[selpos]);
        
        Button ok = (Button)findViewById(R.id.acceditor_ok); 
        if(modeCreate){
            ok.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_add,0,0,0);
            ok.setText(R.string.cact_create);
        }else{
            ok.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_update,0,0,0);
            ok.setText(R.string.cact_update);
        }
        ok.setOnClickListener(this);
        findViewById(R.id.acceditor_cancel).setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.acceditor_ok:
            doOk();
            break;
        case R.id.acceditor_cancel:
            doCancel();
            break;
        }
    }
    
    private void doOk(){
        Logger.d("acceditor doOK");   
        
        workingAccount.setType(AccountType.getSupportedType()[typeEditor.getSelectedItemPosition()].getType());
        workingAccount.setName(nameEditor.getText().toString());
        workingAccount.setInitialValue(Formats.string2Double(initvalEditor.getText().toString()));
        
        if(listener==null || listener.onDialogFinish(this,findViewById(R.id.acceditor_ok), null)){
            dismiss();
        }
    }
    
    private void doCancel(){
        Logger.d("acceditor doCancel");
        if(listener==null || listener.onDialogFinish(this,findViewById(R.id.acceditor_cancel), null)){
            dismiss();
        }
    }
    
    private void checkType(AccountType type){
        boolean enableInitval = !(type==AccountType.INCOME || type==AccountType.EXPENSE);
        initvalEditor.setEnabled(enableInitval);
        if(!enableInitval){
            initvalEditor.setText("0");
        }
    }



}