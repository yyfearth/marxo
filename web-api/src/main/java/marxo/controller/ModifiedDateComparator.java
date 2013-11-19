package marxo.controller;

import marxo.entity.workflow.Workflow;

import java.util.Comparator;

public class ModifiedDateComparator implements Comparator<Workflow> {
	@Override
	public int compare(Workflow w1, Workflow w2) {
		return w1.modifiedDate.compareTo(w2.modifiedDate);
	}
}