package psd.utils;

import psd.Element;

public interface ElementFilter extends Filter<Element> {
	public boolean accept(Element element);
}
