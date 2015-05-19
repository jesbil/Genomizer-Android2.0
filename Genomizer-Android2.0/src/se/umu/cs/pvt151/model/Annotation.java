package se.umu.cs.pvt151.model;

import java.util.ArrayList;

/**
 * Annotation representation class for the Genomizer Android Application,
 * used as a data container. 
 * 
 * @author Erik Åberg, c11ean
 *
 */
public class Annotation {
	
	private String name;
	private ArrayList<String> values;
	private boolean forced;
	
	public boolean isForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}



	/**
	 * Creates a new Annotation object.
	 */
	public Annotation() {
		values = new ArrayList<String>();
	}
	


	/**
	 * Returns the name for the Annotation object.
	 * 
	 * @return the name as a string
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Sets the name for the Annotation object.
	 * 
	 * @param name the name to set as a string
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a list of the values of the current Annotation object.
	 * 
	 * @return values in an ArrayList containing strings
	 */
	public ArrayList<String> getValues() {
		return values;
	}

	/**
	 * Sets the values for the current Annotation object
	 * 
	 * @param value the values to set as a ArrayList of strings
	 */
	public void setValues(ArrayList<String> value) {
		this.values = value;
	}
	
	/**
	 * Appends a value string to the valueList in the current Annotation object
	 * 
	 * @param newValue the string to append to the existing list in the object
	 */
	public void appendValue(String newValue) {
		values.add(newValue);
	}

	/**
	 * Checks if the ValueList in the Annotation object is empty or not.
	 * 
	 * 
	 * @return true if the list is empty, otherwise returns false.
	 */
	public boolean isFreeText() {
		return values.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (forced ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annotation other = (Annotation) obj;
		if (forced != other.forced)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if(values.size()!=other.values.size()){
			return false;
		} else{
			for(int i=0; i<values.size();i++){
				if(!values.get(i).equals(other.values.get(i))){
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name+" ");
		for(String value : values){
			sb.append(value+" ");
		}
		sb.append(String.valueOf(forced));
		return sb.toString();
		
	}
}
