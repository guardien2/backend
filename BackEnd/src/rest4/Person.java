package rest4;




public class Person {

private int id;
private String fornamn;
private String efternamn;
private int fodelsear;

public Person(int id, String fornamn, String efternamn, int fodelsear) {
	this.id = id;
    this.fornamn = fornamn;
	this.efternamn = efternamn;
	this.fodelsear = fodelsear;
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getFornamn() {
	return fornamn;
}

public void setFornamn(String fornamn) {
	this.fornamn = fornamn;
}

public String getEfternamn() {
	return efternamn;
}

public void setEfternamn(String efternamn) {
	this.efternamn = efternamn;
}

public int getFodelsear() {
	return fodelsear;
}

public void setFodelsear(int fodelsear) {
	this.fodelsear = fodelsear;
}


}
