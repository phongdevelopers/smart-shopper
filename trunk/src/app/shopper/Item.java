package app.shopper;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Order;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

@Order(attributes={"name", "onShoppingList", "oneTime"})
public class Item implements OnCheckedChangeListener,Comparable<Item>,OnClickListener{
	@Attribute
	private String name;
	
	@Attribute
	private boolean onShoppingList = true;
	
	@Attribute
	private boolean oneTime;
	private boolean deleteMe = false;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOnShoppingList() {
		return onShoppingList;
	}

	public void setOnShoppingList(boolean onShoppingList) {
		this.onShoppingList = onShoppingList;
	}
	
	View draw(boolean shoppingList){
		String name =this.name ;
		if(oneTime) name += " (once)";		
		if(shoppingList){
			if(onShoppingList){
				TextView tView = new TextView(ShopperFragmentActivity.con);
				tView.setText(name);
				tView.setClickable(true);
				tView.setOnClickListener(this);
				tView.setTextSize(25);
				tView.setLineSpacing(1, (float) 1.5);
				return tView;
			}else 
				return null;
		}else{
			CheckBox check=new CheckBox(ShopperFragmentActivity.con);
			check.setText(name);
			check.setChecked(onShoppingList);
			check.setOnCheckedChangeListener(this);
			return check;
		}		
	}	

	//for simple xml
	public Item() {
		this(null);
	}
	
	public Item(String name) {
		this(name,false);
	}
	
	public Item(String name, Boolean oneTime){
		this.name = name;
		this.oneTime = oneTime;
	}
	
	public Item(String name, Boolean oneTime, boolean onShoppingList){
		this(name,oneTime);		
		setOnShoppingList(onShoppingList);
	}
	
	public void toBuy(){
		onShoppingList = true;
	}
	
	public void bought(){
		onShoppingList =false;
		if(oneTime)deleteMe=true;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		ShopperFragmentActivity.debug("name"+ " "+ isChecked);
		onShoppingList = isChecked;
	}

	@Override
	public int compareTo(Item another) {
		return(name.compareToIgnoreCase(another.name));
	}

	@Override
	public void onClick(View v) {
		bought();
		v.setVisibility(View.GONE);
	}


	public boolean isValid() {
/*		if(oneTime&&!display)
			return false;
		else return true;*/
		return !deleteMe;
	}
}
