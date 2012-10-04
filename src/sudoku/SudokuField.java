
package sudoku;

import java.util.ArrayList;

/**
 * Class represents a single field on the board
 * @author Ondrej Nebesky
 */
public class SudokuField {
    int value;
    ArrayList<Integer> candidates;
    int position = 0;
    boolean generated = true;

    public SudokuField(){
        candidates = new ArrayList<Integer>();
        value = 0;
    }

    /**
     * Constructor
     * @param i default value of the field
     */
    public SudokuField(int i){
        candidates = new ArrayList<Integer>();
        value = i;
    }

    public SudokuField(int i, int position){
        candidates = new ArrayList<Integer>();
        value = i;
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



    @Override
    protected Object clone() throws CloneNotSupportedException {
        SudokuField newField = new SudokuField(this.value, this.position);
        newField.setCandidates((ArrayList<Integer>)candidates.clone());
        return newField;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()){
            if (this.value == ((SudokuField)obj).getValue()) return true;
        }else{
            if (this.value == ((Integer)obj).intValue()) return true;
        }
        return false;
    }

    public void setValue(int value) {
        this.value = value;
        removeCandidate(value);
    }

    /**
     * Set possible values fillable in this field
     * @param possibleValues 
     */
    public void setCandidates(ArrayList<Integer> possibleValues) {
        this.candidates = possibleValues;
    }

    public void addCandidate(int candidate){
        if (!containsCandidate(candidate)) candidates.add(candidate);
    }

    /**
     * Removes an candidate from the possible values
     * @param candidate 
     */
    public void removeCandidate(int candidate){
        for (int i = 0; i < candidates.size(); i++){
            if (candidates.get(i) == candidate){
                candidates.remove(i);
                break;
            }
        }
    }

    /** 
     * Returns intersection of input and candidates for this field 
     */
    public ArrayList<Integer> getMatchingCandidates(ArrayList<Integer> input){
        ArrayList<Integer> output = new ArrayList();
        for (int i = 0; i < input.size(); i++){
            if (containsCandidate(input.get(i))){
                output.add(input.get(i));
            }
        }
        return output;
    }

    public boolean containsCandidate(int candidate){
        for (Integer i : candidates){
            if (i == candidate){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return value + "";
    }

    /**
     * Delete all the values
     */
    public void clear(){
        value = 0;
        position = 0;
        candidates.clear();
    }

    public boolean isInArayList(ArrayList<Integer> values){
        for (Integer i : values){
            if (i == this.value){
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty(){
        if (this.value == 0){
            return true;
        }
        return false;
    }

    public ArrayList<Integer> getCandidates() {
        return candidates;
    }

    public int getSizeOfCandidates(){
        return candidates.size();
    }

    public int getCandidate(int index){
        return candidates.get(index);
    }

}
