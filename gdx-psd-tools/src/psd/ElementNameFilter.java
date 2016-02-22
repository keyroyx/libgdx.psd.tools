package psd;

public class ElementNameFilter implements ElementFilter {
	protected String layerName;

	public ElementNameFilter(String layerName) {
		this.layerName = layerName;
	}

	public boolean accept(Element element) {
		if(element != null && element.layerName != null){
			return element.layerName.equals(layerName);
		}
		return false;
	}
}
