package io.github.fvarrui.javapackager.model;

public class Template {

	private String name;
	private boolean bom = false;
	
	public Template() {}
	
	public Template(String name, boolean bom) {
		this.name = name;
		this.bom = bom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBom() {
		return bom;
	}

	public void setBom(boolean bom) {
		this.bom = bom;
	}

	@Override
	public String toString() {
		return "Template [name=" + name + ", bom=" + bom + "]";
	}

}
