package app.shopper;

public class ItemListFragment extends ShopperFragment {

	@Override
	protected int getLayoutID() {
		return R.layout.itemlist;
	}

	@Override
	protected void refreshData(ShopperFragmentActivity parentActivity) {
		parentActivity.updateItemListDisplay();		
	}	
}
