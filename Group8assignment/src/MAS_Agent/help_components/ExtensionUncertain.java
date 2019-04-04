package MAS_Agent.help_components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Objective;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.AdditiveUtilitySpaceFactory;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.Evaluator;
import genius.core.utility.EvaluatorDiscrete;

public class ExtensionUncertain extends AdditiveUtilitySpaceFactory {
	private AdditiveUtilitySpace u;
	
	public ExtensionUncertain(Domain d) {
		super(d);
		List<Issue> issues = d.getIssues();
		int noIssues = issues.size();
		Map<Objective, Evaluator> evaluatorMap = new HashMap<Objective, Evaluator>();
		for (Issue i : issues) {
			IssueDiscrete issue = (IssueDiscrete) i;
			EvaluatorDiscrete evaluator = new EvaluatorDiscrete();
			evaluator.setWeight(1.0 / noIssues);
			for (ValueDiscrete value : issue.getValues()) {
				evaluator.setEvaluationDouble(value, 0.0);
			}
			evaluatorMap.put(issue, evaluator);
		}
		u = new AdditiveUtilitySpace(d, evaluatorMap);
	}

	// method added to be able to request weights of issues
	public double getWeight(Issue i)
	{
		EvaluatorDiscrete evaluator = (EvaluatorDiscrete) u.getEvaluator(i);
		return evaluator.getWeight();
	}
}
