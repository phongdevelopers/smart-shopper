package app.shopper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class shopper extends Activity implements OnCheckedChangeListener, OnClickListener {
    static Context con;
    ItemList itemList ;
    int layout;

	/** Called when the activity is first created. */
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Read file
        //Display Lists
        Context con = this.getApplicationContext();
        shopper.con = con;
        itemList =new ItemList();
        /*//CheckBox check[] = null ;
        for(int i =0;i<10;i++){
		CheckBox check =  new CheckBox(this);
		check.setText("Hello"+i); 
		ll.addView(check);
		check.setOnCheckedChangeListener(this);
        //setContentView(R.layout.main);
		}
        
        //item = new Item("stuff");
        //setContentView(R.layout.main);
        //ViewGroup display = (ViewGroup) this.findViewById(R.id.Button01);

        //if(display!=null)
        //ll.addView(item.draw(false));
        //R.layout.main);*/
        displayItemList();
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		Log.d("Smart Shopper", "click" + buttonView.getText()+" "+ isChecked);
		System.out.println("sda");
	}
	
	public void displayItemList(){
		setContentViewCustom(R.layout.main);		 
			ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
			sc.addView(itemList.display(false));		
			this.findViewById(R.id.Button01).setOnClickListener(this);
			this.findViewById(R.id.Button02).setOnClickListener(this);
	}
	
	public void displayShoppingList(){
		setContentViewCustom(R.layout.main);
			ScrollView sc=(ScrollView) this.findViewById(R.id.ScrollView01);
			sc.addView(itemList.display(true));		
			this.findViewById(R.id.Button01).setOnClickListener(this);
			this.findViewById(R.id.Button02).setOnClickListener(this);
	}
	
	public void displayNewItem(){
		setContentViewCustom(R.layout.newitem);
		this.findViewById(R.id.Button01).setOnClickListener(this);
		this.findViewById(R.id.Button02).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int button = v.getId();
		Log.d("Smart Shopper", "Layout - " + layout+"  Button - "+button);
		switch(layout){
		case R.layout.main:
			switch(button){
			case R.id.Button01:break;
			case R.id.Button02:displayNewItem();break;
			}
			break;
		case R.layout.newitem:
			switch(button){
			case R.id.Button01:itemList.addItem(new Item(((EditText) this.findViewById(R.id.EditText01)).getText().toString()));break;
			case R.id.Button02:break;
			}
			displayItemList();break;
		}		
	}
	
	public void setContentViewCustom(int id){
		setContentView(id);
		layout = id;
	}
    
}