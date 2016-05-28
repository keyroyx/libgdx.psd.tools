package psd.lg0311;

import psd.reflect.PsdGroup;

public class PsdGroupProxy extends GroupProxy {
	public PsdGroupProxy(PsdGroup group, String path) {
		this((PsdGroup) group.findActor(path));
	}

	public PsdGroupProxy(PsdGroup group) {
		super(group);
	}

	public PsdGroup getPsdGroup() {
		return (PsdGroup) getSource();
	}
}
