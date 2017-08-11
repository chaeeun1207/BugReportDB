package sewon;

public class FieldSet{
	String key;
	int num;
	Field field;
	Field.Refield refield;
	Field f1; Field f2; Field f3; Field f4;
	Field avg; Field min; Field max;
	
	public static Field create_initField(String key, int num){
		return new Field(key,0,0,0,0,0,0,0,0,num);
	}

	public void set_Fieldset(String key, int num){
		field = create_initField(key, num); //for Q1
		refield = new Field.Refield(key, 0, 0, 0, 0, 0, num); //for Q2

		f1 = create_initField(key, num); //for Q3
		f2 = create_initField(key, num);
		f3 = create_initField(key, num);
		f4 = create_initField(key, num);
	
		avg = create_initField(key, num); //for Q4
		min = create_initField(key, num);
		max = create_initField(key, num);
	}

	public FieldSet() {
		super();
	}

}
