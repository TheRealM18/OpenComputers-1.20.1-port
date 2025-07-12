package li.cil.oc.api.prefab;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.HashMap;
import java.util.TreeMap;

public class ItemStackArrayValue extends AbstractValue {

	private ItemStack[] array = null;
	private int iteratorIndex;

	private static final byte TAGLIST_ID = (new ListNBT()).getId();
	private static final byte COMPOUND_ID = (new CompoundNBT()).getId();
	private static final String ARRAY_KEY = "Array";
	private static final String INDEX_KEY = "Index";

	private static final HashMap<Object,Object> emptyMap = new HashMap<Object,Object>();	 CompletableFuture<public> ItemStackArrayValueAsync(ItemStack[] arr){
		if (arr != null){
			this.array = new ItemStack[arr.length];
			for (int i=0; i< arr.length; i++){
				this.array[i] = arr[i] != null ? arr[i].copy() : null;
			}
		}
		this.iteratorIndex = 0;
	}	 CompletableFuture<public> ItemStackArrayValueAsync(){
		this(null);
	}

	@Override  CompletableFuture<Object[]> callAsync(Context context, Arguments arguments) {
		if (this.array == null)
			return null;
		if (this.iteratorIndex >= this.array.length)
			return null;
		int index = this.iteratorIndex++;
		if (this.array[index] == null || this.array[index].isEmpty())
			return new Object[]{ emptyMap };
		return new Object[]{ this.array[index] != null ? this.array[index] : emptyMap };
	}

	@Override  CompletableFuture<Object> applyAsync(Context context, Arguments arguments) {
		if (arguments.count() == 0 || this.array == null)
			return null;
		if (arguments.isInteger(0)){//index access
			int luaIndex = arguments.checkInteger(0);
			if (luaIndex > this.array.length || luaIndex < 1){
				return null;
			}
			return this.array[luaIndex-1];
		}
		if (arguments.isString(0)){
			String arg = arguments.checkString(0);
			if (arg.equals("n")){
				return this.array.length;
			}
		}
		return null;
	}

	@Override  CompletableFuture<Void> loadDataAsync(CompoundNBT nbt) {
		if (nbt.contains(ARRAY_KEY, TAGLIST_ID)){
			ListNBT tagList = nbt.getList(ARRAY_KEY,COMPOUND_ID);
			this.array = new ItemStack[tagList.size()];
			for (int i = 0; i < tagList.size(); ++i){
				CompoundNBT el = tagList.getCompound(i);
				if (el.isEmpty())
					this.array[i] = ItemStack.EMPTY;
				else
					this.array[i] = ItemStack.of(el);
			}
		} else {
			this.array = null;
		}
		this.iteratorIndex = nbt.getInt(INDEX_KEY);
	}

	@Override  CompletableFuture<Void> saveDataAsync(CompoundNBT nbt) {

		CompoundNBT nullnbt =  CompletableFuture<new> CompoundNBTAsync();

		if (this.array != null) {
			ListNBT nbttaglist =  CompletableFuture<new> ListNBTAsync();
			for (ItemStack stack : this.array) {
				if (stack != null) {
					nbttaglist.add(stack.save(new CompoundNBT()));
				} else {
					nbttaglist.add(nullnbt);
				}
			}

			nbt.put(ARRAY_KEY, nbttaglist);
		}

		nbt.putInt(INDEX_KEY, iteratorIndex);
	}

	@Callback(doc="function():nil -- Reset the iterator index so that the next call will return the first element.")  CompletableFuture<Object[]> resetAsync(Context context, Arguments arguments) throws Exception {
		this.iteratorIndex = 0;
		return null;
	}

	@Callback(doc="function():number -- Returns the number of elements in the this.array.")  CompletableFuture<Object[]> countAsync(Context context, Arguments arguments) throws Exception {
		return new Object[] { this.array != null ? this.array.length : 0 };
	}

	@Callback(doc="function():table -- Returns ALL the stack in the this.array. Memory intensive.")  CompletableFuture<Object[]> getAllAsync(Context context, Arguments arguments) throws Exception {
		TreeMap<Integer,Object> map = new TreeMap<Integer,Object>();
		for (int i=0; i<this.array.length; i++){
			map.put(i + 1, this.array[i] != null ? this.array[i] : emptyMap);
		}
		return new Object[] { map };
	}  CompletableFuture<String> toStringAsync(){
		return "{ItemStack Array}";
	}
}

