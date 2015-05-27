package apapl.program;

import apapl.APLModule;
import apapl.NoRuleException;
import apapl.Parser;
import apapl.plans.BeliefUpdateAction;
import apapl.plans.ChunkPlan;
import apapl.plans.Plan;
import apapl.plans.PlanResult;
import apapl.plans.PlanSeq;
import apapl.program.PGrule;
import apapl.data.APLFunction;
import apapl.data.Goal;
import apapl.data.Literal;
import apapl.data.Prohibition;
import apapl.data.Term;
import apapl.data.Query;
import apapl.program.Rule;
import apapl.data.True;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import apapl.SubstList;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.sun.tools.javac.util.Pair;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;

public class PGrulebase extends Rulebase<PGrule> {

	/**
	 * Equal to
	 * {@link apapl.program.PGrulebase#generatePlans(goalbase,beliefbase,planbase,atomicplans,false)}
	 * 
	 * @param atomicplans
	 * @param prohibitions
	 * 
	 *            public ArrayList<PlanSeq> generatePlans(Goalbase goalbase,
	 *            Beliefbase beliefbase, Planbase planbase) { return
	 *            generatePlans(goalbase,beliefbase,planbase,false); }
	 * 
	 *            /** NEVER CALLED Equal to
	 *            {@link apapl.program.PGrulebase#generatePlans(goalbase,beliefbase,planbase,atomicplans,true)}
	 * 
	 *            public ArrayList<PlanSeq> generatePlan(Goalbase goalbase,
	 *            Beliefbase beliefbase, Planbase planbase) { return
	 *            generatePlans(goalbase,beliefbase,planbase,true); }
	 */
	/**
	 * Returns a list of plans that can be generated by applying the PG rules. A
	 * PG-rule generates a plan if the head matches some goal and if the guard
	 * is satisfied by the beliefs and if the module is not already working on a
	 * plan that has been generated by the same rule for the same goal. In
	 * generating plans, each PG-rule is tried only once.
	 * 
	 * @param goalbase
	 *            goalbase that is needed to select a rule
	 * @param beliefbase
	 *            beliefbase that is needed to select a rule
	 * @param planbase
	 *            planbase that is needed to check whether a rule may be
	 *            selected
	 * @param prohibitions
	 * @param onlyone
	 *            if true, only one plan will be generated
	 * @return an list containing one or more plans that can be generate with
	 *         the PG rules
	 */
	/*
	 * public ArrayList<PlanSeq> generatePlans(Goalbase goalbase, Beliefbase
	 * beliefbase, Planbase planbase, boolean onlyone) { ArrayList<PlanSeq>
	 * plans = new ArrayList<PlanSeq>();
	 * 
	 * // for each rule for (PGrule pgrule : rules) { // if it is a reactive
	 * rule, try to match the guard with the beliefs if (pgrule.getHead()
	 * instanceof True) { SubstList<Term> theta = new SubstList<Term>(); PlanSeq
	 * p = tryRule(pgrule.clone(),pgrule,theta,beliefbase,planbase); if
	 * (p!=null) { plans.add(p); planbase.addPlan(p); if (onlyone) return plans;
	 * } } // if it is not a reactive rule, try to match the head with the goals
	 * else for (Goal goal : goalbase) { boolean ruleApplied = false;
	 * ArrayList<SubstList<Term>> substs; PGrule variant =
	 * pgrule.getVariant(goal.getVariables()); substs =
	 * goal.possibleSubstitutions(variant.getHead());
	 * 
	 * // for all possible substitutions of the head of the rule, try to match
	 * // it with the guard of the rule and check if the module is not already
	 * // working on a plan for the same goal for (SubstList<Term> theta :
	 * substs) { PlanSeq p = tryRule(variant,pgrule,theta,beliefbase,planbase);
	 * if (p!=null) { ruleApplied = true; plans.add(p); planbase.addPlan(p); if
	 * (onlyone) return plans; break; } }
	 * 
	 * if( ruleApplied ) break; } } return plans; }
	 */
	public ArrayList<PlanSeq> generatePlans(Goalbase goalbase,
			Beliefbase beliefbase, Planbase planbase, boolean onlyone,
			APLModule module) {
		ArrayList<PlanSeq> plans = new ArrayList<PlanSeq>();
		//goalbase.sort();

		// for each rule
		for (PGrule pgrule : rules) { // if it is a reactive rule, try to match
										// the guard with the beliefs
			if (pgrule.getHead() instanceof True) {
				SubstList<Term> theta = new SubstList<Term>();
				PlanSeq p = tryRule(pgrule.clone(), pgrule, theta, beliefbase,
						planbase, null);
				if (p != null) {
					// plans.add(p);
					/*
					 * LinkedList<Plan> pls = p.getPlans();
					 * 
					 * if (pls.size() > 0) { Plan pl = pls.getFirst(); if (pl
					 * instanceof ChunkPlan) { PlanSeq pl1 = ((ChunkPlan)
					 * pl).toPlanSeq(); planbase.addPlan(pl1); plans.add(pl1); }
					 * else {
					 * 
					 * planbase.addPlan(p); plans.add(p); } }
					 */
					plans.add(p);
					// planbase.addPlan(p);
					if (onlyone)
						return plans;
				}
			}
			// if it is not a reactive rule, try to match the head with the
			// goals
			else
				for (Goal goal : goalbase) { // boolean ruleApplied = false;
					ArrayList<SubstList<Term>> substs;
					PGrule variant = pgrule.getVariant(goal.getVariables());
					substs = goal.possibleSubstitutions(variant.getHead());

					// for all possible substitutions of the head of the rule,
					// try to match
					// it with the guard of the rule and check if the module is
					// not already
					// working on a plan for the same goal
					for (SubstList<Term> theta : substs) {
						PlanSeq p = tryRule(variant, pgrule, theta, beliefbase,
								planbase, goal);
						if (p != null) {
							// ruleApplied = true;
							// plans.add(p);

							/*
							 * LinkedList<Plan> pls = p.getPlans();
							 * 
							 * if (pls.size() > 0) { Plan pl = pls.getFirst();
							 * 
							 * if (pl.getParent() instanceof ChunkPlan) {
							 * PlanSeq pl1 = ((ChunkPlan)
							 * pl.getParent()).toPlanSeq();
							 * planbase.addPlan(pl1); plans.add(pl1); } else {
							 * 
							 * planbase.addPlan(p); plans.add(p); } }
							 */

							// Object atomic = p.getPlans().getFirst();

							plans.add(p);
							// planbase.addPlan(p);
							if (onlyone)
								return plans;
							break;
						}
					}

					// if( ruleApplied ) break;
				}
		}
		System.out.println("[Planbase]:"+ planbase.toString());
		ArrayList<PlanSeq> plansReturn = plans;
		for (PlanSeq x : planbase)
			plansReturn.add(x);
		
		System.out.println("[new scheduled]:"+ plans.toString());
		ArrayList<PlanSeq> plansFinal = this.schedulePlans(module, plansReturn);
		for (PlanSeq p : plansFinal)
			planbase.addPlan(p);
		planbase.replacePlans(plans);
		System.out.println("[After]:"+ planbase.toString());
		return plansReturn;
	}

	/**
	 * Tries to apply a PG-rule given substitution theta.
	 * 
	 * @param pgrule
	 *            the PGrule to be applied
	 * @param theta
	 *            the substitutions that are needed to match the rule
	 * @param beliefs
	 *            belief base
	 * @param plans
	 *            plan base
	 * @return the body of the rule with theta applied to it or null if this
	 *         rule cannot be applied
	 * 
	 *         private PlanSeq tryRule(PGrule variant, PGrule pgrule,
	 *         SubstList<Term> theta, Beliefbase beliefs, Planbase planbase) {
	 *         variant.applySubstitution(theta); Query goalquery =
	 *         variant.getHead(); Query beliefquery = variant.getGuard();
	 *         SubstList<Term> goaltheta = clone(theta); // Goal is still a goal
	 *         of the module if (!beliefs.doQuery(goalquery,theta)||goalquery
	 *         instanceof True) // Guard is satisfied if
	 *         (beliefs.doQuery(beliefquery,theta)) { PlanSeq p =
	 *         variant.getBody(); p.applySubstitution(theta);
	 *         p.setActivationRule(pgrule); p.setActivationGoal(goaltheta);
	 *         p.setActivationSubstitution(theta);
	 * 
	 * 
	 *         // For the special case we are dealing with a reactive rule (head
	 *         is True) // it should not be the case that the module is working
	 *         on an instance of // the same rule if( goalquery instanceof True
	 *         && !planbase.ruleOccurs( p.getActivationRule() ) ) { return p; }
	 *         // Otherwise, the module might not be already working on a plan
	 *         that was // generated by the same rule for this goal else if
	 *         (!planbase.sameRuleActiveForSameGoal( pgrule, theta ) ) { return
	 *         p; } } return null; }
	 */
	private PlanSeq tryRule(PGrule variant, PGrule pgrule,
			SubstList<Term> theta, Beliefbase beliefs, Planbase planbase,
			Goal goal) {
		variant.applySubstitution(theta);
		Query goalquery = variant.getHead();
		Query beliefquery = variant.getGuard();
		SubstList<Term> goaltheta = clone(theta);
		// Goal is still a goal of the module
		if (!beliefs.doQuery(goalquery, theta) || goalquery instanceof True)
			// Guard is satisfied
			if (beliefs.doQuery(beliefquery, theta)) {
				PlanSeq p = variant.getBody();
				p.applySubstitution(theta);
				p.setActivationRule(pgrule);
				p.setActivationGoal(goaltheta);
				p.setActivationSubstitution(theta);
				p.setAtomic(pgrule.getBody().isAtomic());
				p.setDuration(pgrule.getDuration());
				if (goal != null) {
					p.setDeadline(goal.getDeadline());
					p.setPriority(goal.getPriority());
				}

				// For the special case we are dealing with a reactive rule
				// (head is True)
				// it should not be the case that the module is working on an
				// instance of
				// the same rule
				if (goalquery instanceof True
						&& !planbase.ruleOccurs(p.getActivationRule())) {
					return p;
				}
				// Otherwise, the module might not be already working on a plan
				// that was
				// generated by the same rule for this goal
				else if (!planbase.sameRuleActiveForSameGoal(pgrule, theta)) {
					return p;
				}
			}
		return null;
	}

	/**
	 * Clones a substitution.
	 * 
	 * @note this is not the place for this function. It should be defined in
	 *       SubstList.
	 * 
	 * @param theta
	 *            the substitution to be cloned.
	 * @return the cloned substitution
	 */
	private SubstList<Term> clone(SubstList<Term> theta) {
		SubstList<Term> theta2 = new SubstList<Term>();
		theta2.putAll(theta);
		return theta2;
	}

	/**
	 * @return clone of the PG rulebase
	 */
	public PGrulebase clone() {
		PGrulebase b = new PGrulebase();
		b.setRules(getRules());
		return b;
	}

	private ArrayList<PlanSeq> schedulePlans(APLModule module,
			ArrayList<PlanSeq> planbasetmp) {

		ArrayList<PlanSeq> newAtomic = new ArrayList<PlanSeq>();

		ArrayList<PlanSeq> newNonAtomic = new ArrayList<PlanSeq>();

		ArrayList<PlanSeq> planbase = sortPlansPriority(planbasetmp);

		HashMap<Integer,Long> cycleExecStart = new HashMap<Integer,Long>();

		for (PlanSeq ps : planbase) {

			// atomic

			ArrayList<PlanSeq> tempAtomic = new ArrayList<PlanSeq>();

			if (ps.isAtomic()) {
				
				
				if (ps == module.getAtomic()) {
					if (ps.getExecStart() == null)
						ps.setExecStart(null);
					cycleExecStart.put(ps.getID(), System.currentTimeMillis());
					tempAtomic = newAtomic;
				} else {
					Long ne = System.currentTimeMillis();
					tempAtomic = new ArrayList<PlanSeq>();

					for (PlanSeq p : newAtomic) {
						if (p.getDeadline().getTime() <= ps.getDeadline().getTime()) {
							tempAtomic.add(p);
							if (p.getExecStart() == null)
								ne += p.getDuration();
							else
								ne = Math.max(p.getExecStart().getTime() + System.currentTimeMillis() + p.getDuration(), ne);
						} else {
							Long old = cycleExecStart.get(p.getID());
							if (ps.getExecStart() == null) {
								//long old = cycleExecStart.get(p.getID());
								//if (old != null)
								cycleExecStart.put(p.getID(), old==null?0:old + ps.getDuration());
							}
							else
								cycleExecStart.put(p.getID(), old==null?0:old + ps.getExecStart().getTime() + System.currentTimeMillis() + ps.getDuration());
							tempAtomic.add(p);
						}
					}

					cycleExecStart.put(ps.getID(),ne);
					//ps.setExecStart(ne);
				}

				boolean pass = true;

				if (!violatesProhibitions(ps, module)) {
					for (PlanSeq p1 : tempAtomic) {
						//Date now = new Date();
						long rt = 0;
						
						if (p1.getDeadline().getTime() < System.currentTimeMillis()) { //TODO this should not be needed

							pass = false;
							break;
						}
						for (PlanSeq p2 : tempAtomic) {
							if (cycleExecStart.get(p2.getID()) <= cycleExecStart.get(p1.getID())) {
								if (p2.getExecStart() == null)
									rt += p2.getDuration();
								else
									rt += p2.getDuration() - p2.getExecStart().getTime() + System.currentTimeMillis();
							}
						}

						if (System.currentTimeMillis() + rt >= p1.getDeadline().getTime()) {
							pass = false; // TODO algorithm not working
							break;
						}
					}
				} else {
					pass = false;
					ps.setProhibited();
				}

				if (pass)
					newAtomic.add(ps);

				// non atomic
			} else {
				Date now = new Date();

				if (passNorms(ps, module)) {
					if (ps.getDeadline() == null) {
						ps.setExecStart(null);
						newNonAtomic.add(ps);	
					}else if (ps.getDeadline() != null && ps.getDeadline().getTime() < now.getTime()) {
						ps.setExecStart(null);
						newNonAtomic.add(ps);	
					}
					else if (ps.getExecStart() != null) {
						if (ps.getDuration() + ps.getExecStart().getTime() <= ps
								.getDeadline().getTime())
							newNonAtomic.add(ps);
					} else if (ps.getDeadline() != null
							&& now.getTime() + ps.getDuration() <= ps
									.getDeadline().getTime()) {
						ps.setExecStart(null);
						newNonAtomic.add(ps);
					}

					// ps.setExecStart(null); //TODO remove
					// newNonAtomic.add(ps);
				}

			}
		}

		planbase = new ArrayList<PlanSeq>();

		
		//boolean first = true;
		for (PlanSeq ps1 : newAtomic) {
			if (!ps1.getProhibited() ) {
				//module.setAtomic(ps1);
				ps1.setExecStart(null);
				//first = false;
			}
			planbase.add(ps1);

		}
		boolean isFirst = false;
		for (PlanSeq ps : planbase) {
			if (module.getAtomic() != null && module.getAtomic().equals(ps))
				isFirst = true;
		}
		if (!isFirst && newAtomic.size() > 0)
			module.setAtomic(planbase.get(0));
		
		for (PlanSeq ps1 : newNonAtomic) {
			planbase.add(ps1);
		}
		return planbase;

	}

	public ArrayList<PlanSeq> sortPlansPriority(ArrayList<PlanSeq> plans) {

		// ArrayList<PlanSeq> copy = (ArrayList<PlanSeq>) plans.clone();
		Collections.sort(plans, new Comparator<PlanSeq>() {
			public int compare(PlanSeq p1, PlanSeq p2) {
				return p2.getPriority().compareTo(p1.getPriority());
			}
		});
		return plans;
	}

	private boolean passNorms(PlanSeq ps, APLModule module) {

		if (violatesProhibitions(ps, module))
			return false;

		Date deadline = ps.getDeadline();
		if (deadline == null || deadline.getTime() == Long.MAX_VALUE)
			return true;

		Date started = ps.getExecStart();
		Date now = new Date();
		if (started == null) {
			if (ps.getDuration() + now.getTime() > deadline.getTime())
				return false;
		} else {
			long executed = now.getTime() - started.getTime();
			if (ps.getDuration() - executed > deadline.getTime()
					- now.getTime())
				return false;
		}

		return true;
	}

	private boolean violatesProhibitions(PlanSeq ps, APLModule module) {

		ArrayList<Prohibition> hpp = module.getProhibitionbase().getHigher(
				ps.getPriority());

		if (hpp != null) {
			for (Prohibition p : hpp) {
				try {
					if (existIn(ps, p, module))
						return true;
				} catch (NoRuleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private boolean existIn(PlanSeq ps, Prohibition pp, APLModule module)
			throws NoRuleException {

		for (Plan plan : ps.getPlans()) {
			// plan.evaluateArguments();
			if (plan instanceof BeliefUpdateAction) {
				APLFunction p = ((BeliefUpdateAction) plan).getPlan();
				SubstList<Term> theta = new SubstList<Term>();
				Beliefbase beliefbase = module.getBeliefbase();
				BeliefUpdate c;
				c = module.getBeliefUpdates().selectBeliefUpdate(p, beliefbase,
						theta);

				if (c == null)
					continue;

				for (Literal l : c.getPost()) {
					Literal lcopy = l.clone();
					lcopy.applySubstitution(theta);
					Literal pl = pp.getProhibition();
					// System.out.println(lcopy.toString() +"  "+pl.toString());
					if (lcopy.equals(pl))
						return true;
				}
			}
		}
		return false;
	}

}
///*
// * 
// * 
// * package apapl.program;
//
//import apapl.APLModule;
//import apapl.NoRuleException;
//import apapl.Parser;
//import apapl.plans.BeliefUpdateAction;
//import apapl.plans.ChunkPlan;
//import apapl.plans.Plan;
//import apapl.plans.PlanResult;
//import apapl.plans.PlanSeq;
//import apapl.program.PGrule;
//import apapl.data.APLFunction;
//import apapl.data.Goal;
//import apapl.data.Literal;
//import apapl.data.Prohibition;
//import apapl.data.Term;
//import apapl.data.Query;
//import apapl.program.Rule;
//import apapl.data.True;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
//import apapl.SubstList;
//
//import javax.swing.JComponent;
//import javax.swing.JTextArea;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//
//import com.sun.tools.javac.util.Pair;
//
//import java.awt.GridLayout;
//import java.awt.BorderLayout;
//import java.awt.Color;
//
//public class PGrulebase extends Rulebase<PGrule> {
//
//	/**
//	 * Equal to
//	 * {@link apapl.program.PGrulebase#generatePlans(goalbase,beliefbase,planbase,atomicplans,false)}
//	 * 
//	 * @param atomicplans
//	 * @param prohibitions
//	 * 
//	 *            public ArrayList<PlanSeq> generatePlans(Goalbase goalbase,
//	 *            Beliefbase beliefbase, Planbase planbase) { return
//	 *            generatePlans(goalbase,beliefbase,planbase,false); }
//	 * 
//	 *            /** NEVER CALLED Equal to
//	 *            {@link apapl.program.PGrulebase#generatePlans(goalbase,beliefbase,planbase,atomicplans,true)}
//	 * 
//	 *            public ArrayList<PlanSeq> generatePlan(Goalbase goalbase,
//	 *            Beliefbase beliefbase, Planbase planbase) { return
//	 *            generatePlans(goalbase,beliefbase,planbase,true); }
//	 */
//	/**
//	 * Returns a list of plans that can be generated by applying the PG rules. A
//	 * PG-rule generates a plan if the head matches some goal and if the guard
//	 * is satisfied by the beliefs and if the module is not already working on a
//	 * plan that has been generated by the same rule for the same goal. In
//	 * generating plans, each PG-rule is tried only once.
//	 * 
//	 * @param goalbase
//	 *            goalbase that is needed to select a rule
//	 * @param beliefbase
//	 *            beliefbase that is needed to select a rule
//	 * @param planbase
//	 *            planbase that is needed to check whether a rule may be
//	 *            selected
//	 * @param prohibitions
//	 * @param onlyone
//	 *            if true, only one plan will be generated
//	 * @return an list containing one or more plans that can be generate with
//	 *         the PG rules
//	 */
//	/*
//	 * public ArrayList<PlanSeq> generatePlans(Goalbase goalbase, Beliefbase
//	 * beliefbase, Planbase planbase, boolean onlyone) { ArrayList<PlanSeq>
//	 * plans = new ArrayList<PlanSeq>();
//	 * 
//	 * // for each rule for (PGrule pgrule : rules) { // if it is a reactive
//	 * rule, try to match the guard with the beliefs if (pgrule.getHead()
//	 * instanceof True) { SubstList<Term> theta = new SubstList<Term>(); PlanSeq
//	 * p = tryRule(pgrule.clone(),pgrule,theta,beliefbase,planbase); if
//	 * (p!=null) { plans.add(p); planbase.addPlan(p); if (onlyone) return plans;
//	 * } } // if it is not a reactive rule, try to match the head with the goals
//	 * else for (Goal goal : goalbase) { boolean ruleApplied = false;
//	 * ArrayList<SubstList<Term>> substs; PGrule variant =
//	 * pgrule.getVariant(goal.getVariables()); substs =
//	 * goal.possibleSubstitutions(variant.getHead());
//	 * 
//	 * // for all possible substitutions of the head of the rule, try to match
//	 * // it with the guard of the rule and check if the module is not already
//	 * // working on a plan for the same goal for (SubstList<Term> theta :
//	 * substs) { PlanSeq p = tryRule(variant,pgrule,theta,beliefbase,planbase);
//	 * if (p!=null) { ruleApplied = true; plans.add(p); planbase.addPlan(p); if
//	 * (onlyone) return plans; break; } }
//	 * 
//	 * if( ruleApplied ) break; } } return plans; }
//	 */
//	public ArrayList<PlanSeq> generatePlans(Goalbase goalbase,
//			Beliefbase beliefbase, Planbase planbase, boolean onlyone,
//			APLModule module) {
//		ArrayList<PlanSeq> plans = new ArrayList<PlanSeq>();
//
//		System.out.println("[goalbase]:"+ goalbase.toString());
//		
//		// for each rule
//		for (PGrule pgrule : rules) { // if it is a reactive rule, try to match
//			// the guard with the beliefs
//			if (pgrule.getHead() instanceof True) {
//				SubstList<Term> theta = new SubstList<Term>();
//				PlanSeq p = tryRule(pgrule.clone(), pgrule, theta, beliefbase,
//						planbase, null);
//				if (p != null && !violatesProhibitions(p, module)) {
//					// plans.add(p);
//					/*
//					 * LinkedList<Plan> pls = p.getPlans();
//					 * 
//					 * if (pls.size() > 0) { Plan pl = pls.getFirst(); if (pl
//					 * instanceof ChunkPlan) { PlanSeq pl1 = ((ChunkPlan)
//					 * pl).toPlanSeq(); planbase.addPlan(pl1); plans.add(pl1); }
//					 * else {
//					 * 
//					 * planbase.addPlan(p); plans.add(p); } }
//					 */
//					plans.add(p);
//					// planbase.addPlan(p);
//					if (onlyone)
//						return plans;
//				}
//			}
//			// if it is not a reactive rule, try to match the head with the
//			// goals
//			else
//				for (Goal goal : goalbase) { // boolean ruleApplied = false;
//					ArrayList<SubstList<Term>> substs;
//					PGrule variant = pgrule.getVariant(goal.getVariables());
//					substs = goal.possibleSubstitutions(variant.getHead());
//
//					// for all possible substitutions of the head of the rule,
//					// try to match
//					// it with the guard of the rule and check if the module is
//					// not already
//					// working on a plan for the same goal
//					for (SubstList<Term> theta : substs) {
//						PlanSeq p = tryRule(variant, pgrule, theta, beliefbase,
//								planbase, goal);
//						if (p != null && !violatesProhibitions(p, module)) {
//							// ruleApplied = true;
//							// plans.add(p);
//
//							/*
//							 * LinkedList<Plan> pls = p.getPlans();
//							 * 
//							 * if (pls.size() > 0) { Plan pl = pls.getFirst();
//							 * 
//							 * if (pl.getParent() instanceof ChunkPlan) {
//							 * PlanSeq pl1 = ((ChunkPlan)
//							 * pl.getParent()).toPlanSeq();
//							 * planbase.addPlan(pl1); plans.add(pl1); } else {
//							 * 
//							 * planbase.addPlan(p); plans.add(p); } }
//							 */
//
//							// Object atomic = p.getPlans().getFirst();
//
//							plans.add(p);
//							// planbase.addPlan(p);
//							if (onlyone)
//								return plans;
//							break;
//						}
//					}
//
//					// if( ruleApplied ) break;
//				}
//		}
//		System.out.println("[Planbase]:"+ planbase.toString());
//		System.out.println("[new scheduled]:"+ plans.toString());
//		ArrayList<PlanSeq> plansReturn = plans;
//		for (PlanSeq x : planbase)
//			plansReturn.add(x);
//		ArrayList<PlanSeq> plansFinal = this.schedulePlans(module, plansReturn);
//		System.out.println("[After]:"+ plansFinal.toString());
//	//	for (PlanSeq p : plansFinal)
//	//		planbase.addPlan(p);
//		planbase.replacePlans(plansFinal);
//	//	System.out.println("[After]:"+ planbase.toString());
//		return plansFinal;
//	}
//
//	/**
//	 * Tries to apply a PG-rule given substitution theta.
//	 * 
//	 * @param pgrule
//	 *            the PGrule to be applied
//	 * @param theta
//	 *            the substitutions that are needed to match the rule
//	 * @param beliefs
//	 *            belief base
//	 * @param plans
//	 *            plan base
//	 * @return the body of the rule with theta applied to it or null if this
//	 *         rule cannot be applied
//	 * 
//	 *         private PlanSeq tryRule(PGrule variant, PGrule pgrule,
//	 *         SubstList<Term> theta, Beliefbase beliefs, Planbase planbase) {
//	 *         variant.applySubstitution(theta); Query goalquery =
//	 *         variant.getHead(); Query beliefquery = variant.getGuard();
//	 *         SubstList<Term> goaltheta = clone(theta); // Goal is still a goal
//	 *         of the module if (!beliefs.doQuery(goalquery,theta)||goalquery
//	 *         instanceof True) // Guard is satisfied if
//	 *         (beliefs.doQuery(beliefquery,theta)) { PlanSeq p =
//	 *         variant.getBody(); p.applySubstitution(theta);
//	 *         p.setActivationRule(pgrule); p.setActivationGoal(goaltheta);
//	 *         p.setActivationSubstitution(theta);
//	 * 
//	 * 
//	 *         // For the special case we are dealing with a reactive rule (head
//	 *         is True) // it should not be the case that the module is working
//	 *         on an instance of // the same rule if( goalquery instanceof True
//	 *         && !planbase.ruleOccurs( p.getActivationRule() ) ) { return p; }
//	 *         // Otherwise, the module might not be already working on a plan
//	 *         that was // generated by the same rule for this goal else if
//	 *         (!planbase.sameRuleActiveForSameGoal( pgrule, theta ) ) { return
//	 *         p; } } return null; }
//	 */
//	private PlanSeq tryRule(PGrule variant, PGrule pgrule,
//			SubstList<Term> theta, Beliefbase beliefs, Planbase planbase,
//			Goal goal) {
//		variant.applySubstitution(theta);
//		Query goalquery = variant.getHead();
//		Query beliefquery = variant.getGuard();
//		SubstList<Term> goaltheta = clone(theta);
//		// Goal is still a goal of the module
//		if (!beliefs.doQuery(goalquery, theta) || goalquery instanceof True)
//			// Guard is satisfied
//			if (beliefs.doQuery(beliefquery, theta)) {
//				PlanSeq p = variant.getBody();
//				p.applySubstitution(theta);
//				p.setActivationRule(pgrule);
//				p.setActivationGoal(goaltheta);
//				p.setActivationSubstitution(theta);
//				p.setAtomic(pgrule.getBody().isAtomic());
//				p.setDuration(pgrule.getDuration());
//				if (goal != null) {
//					p.setDeadline(goal.getDeadline());
//					p.setPriority(goal.getPriority());
//				}
//
//				// For the special case we are dealing with a reactive rule
//				// (head is True)
//				// it should not be the case that the module is working on an
//				// instance of
//				// the same rule
//				if (goalquery instanceof True
//						&& !planbase.ruleOccurs(p.getActivationRule())) {
//					return p;
//				}
//				// Otherwise, the module might not be already working on a plan
//				// that was
//				// generated by the same rule for this goal
//				else if (!planbase.sameRuleActiveForSameGoal(pgrule, theta)) {
//					return p;
//				}
//			}
//		return null;
//	}
//
//	/**
//	 * Clones a substitution.
//	 * 
//	 * @note this is not the place for this function. It should be defined in
//	 *       SubstList.
//	 * 
//	 * @param theta
//	 *            the substitution to be cloned.
//	 * @return the cloned substitution
//	 */
//	private SubstList<Term> clone(SubstList<Term> theta) {
//		SubstList<Term> theta2 = new SubstList<Term>();
//		theta2.putAll(theta);
//		return theta2;
//	}
//
//	/**
//	 * @return clone of the PG rulebase
//	 */
//	public PGrulebase clone() {
//		PGrulebase b = new PGrulebase();
//		b.setRules(getRules());
//		return b;
//	}
//
//	private ArrayList<PlanSeq> schedulePlans(APLModule module,
//			ArrayList<PlanSeq> planbasetmp) {
//
//		ArrayList<PlanSeq> newAtomic = new ArrayList<PlanSeq>();
//
//		ArrayList<PlanSeq> newNonAtomic = new ArrayList<PlanSeq>();
//
//		ArrayList<PlanSeq> planbase = sortPlansPriority(planbasetmp);
//		
//		
//
//		HashMap<Integer,Long> cycleExecStart = new HashMap<Integer,Long>();
//
//		for (PlanSeq ps : planbase) {
//
//			// atomic
//
//			ArrayList<PlanSeq> tempAtomic = new ArrayList<PlanSeq>();
//
//			if (ps.isAtomic()) {
//
//
//				if (ps == module.getAtomic()) {
//					if (ps.getExecStart() == null)
//						ps.setExecStart(null);
//					cycleExecStart.put(ps.getID(), System.currentTimeMillis());
//					//if (violatesProhibitions(ps, module))
//					//	ps.setProhibited();
//					tempAtomic = newAtomic;
//				} else {
//					Long ne = System.currentTimeMillis();
//					tempAtomic = new ArrayList<PlanSeq>();
//
//					for (PlanSeq p : newAtomic) {
//						if (p.getDeadline().getTime() <= ps.getDeadline().getTime()) {
//							tempAtomic.add(p);
//							if (p.getExecStart() == null)
//								ne += p.getDuration();
//							else
//								ne = Math.max(p.getExecStart().getTime() + System.currentTimeMillis() + p.getDuration(), ne);
//						} else {
//							Long old = cycleExecStart.get(p.getID());
//							if (ps.getExecStart() == null) {
//								//long old = cycleExecStart.get(p.getID());
//								//if (old != null)
//								cycleExecStart.put(p.getID(), old==null?0:old + ps.getDuration());
//							}
//							else
//								cycleExecStart.put(p.getID(), old==null?0:old + ps.getExecStart().getTime() + System.currentTimeMillis() + ps.getDuration());
//							tempAtomic.add(p);
//						}
//					}
//
//					cycleExecStart.put(ps.getID(),ne);
//					//ps.setExecStart(ne);
//				}
//
//				boolean pass = true;
//
//				if (!violatesProhibitions(ps, module)) {
//					for (PlanSeq p1 : tempAtomic) {
//						//Date now = new Date();
//						long rt = 0;
//
//						if (p1.getDeadline().getTime() < System.currentTimeMillis()) { //TODO this should not be needed
//
//							pass = false;
//							break;
//						}
//						for (PlanSeq p2 : tempAtomic) {
//							if (cycleExecStart.get(p2.getID()) <= cycleExecStart.get(p1.getID())) {
//								if (p2.getExecStart() == null)
//									rt += p2.getDuration();
//								else
//									rt += p2.getDuration() - p2.getExecStart().getTime() + System.currentTimeMillis();
//							}
//						}
//
//						if (System.currentTimeMillis() + rt >= p1.getDeadline().getTime()) {
//							pass = false; // TODO algorithm not working
//							break;
//						}
//					}
//				} else {
//					pass = false;
//					ps.setProhibited();
//				}
//
//				if (pass)
//					newAtomic.add(ps);
//
//				// non atomic
//			} else {
//				Date now = new Date();
////				if (!violatesProhibitions(ps, module)) {
//				if (passNorms(ps, module)) {
//					if (ps.getDeadline() == null) {
//						ps.setExecStart(null);
//						newNonAtomic.add(ps);	
//					} else if (ps.getDeadline() != null && ps.getDeadline().getTime() < now.getTime()) {
//						ps.setExecStart(null);
//						newNonAtomic.add(ps);	
//					}
//					else if (ps.getExecStart() != null) {
//						if (ps.getDuration() + ps.getExecStart().getTime() <= ps
//								.getDeadline().getTime())
//							newNonAtomic.add(ps);
//					} else if (ps.getDeadline() != null
//							&& now.getTime() + ps.getDuration() <= ps
//							.getDeadline().getTime()) {
//						ps.setExecStart(null);
//						newNonAtomic.add(ps);
//					}
//
//				//	ps.setExecStart(null); //TODO remove
//				//	newNonAtomic.add(ps);
//				}
//			}
//		}
//
//		planbase = new ArrayList<PlanSeq>();
//
//		for (PlanSeq ps1 : newNonAtomic) {
//			planbase.add(ps1);
//		}
//		boolean first = true;
//		for (PlanSeq ps1 : newAtomic) {
//			if (!ps1.getProhibited() && first) {
//				module.setAtomic(ps1);
//				//ps1.setExecStart(null);
//				first = false;				
//			}
//			planbase.add(ps1);
//		}
//		return planbase;
//	}
//
//	public ArrayList<PlanSeq> sortPlansPriority(ArrayList<PlanSeq> plans) {
//
//		// ArrayList<PlanSeq> copy = (ArrayList<PlanSeq>) plans.clone();
//		Collections.sort(plans, new Comparator<PlanSeq>() {
//			public int compare(PlanSeq p1, PlanSeq p2) {
//				return p1.getPriority().compareTo(p1.getPriority());
//			}
//		});
//		return plans;
//	}
//
//
//	/*	private boolean passNorms(PlanSeq ps, APLModule module) {
//
//		if (violatesProhibitions(ps, module))
//			return false;
//
//		Date deadline = ps.getDeadline();
//		if (deadline == null || deadline.getTime() == Long.MAX_VALUE)
//			return true;
//
//		Date started = ps.getExecStart();
//		Date now = new Date();
//		if (started == null) {
//			if (ps.getDuration() + now.getTime() > deadline.getTime())
//				return false;
//		} else {
//			long executed = now.getTime() - started.getTime();
//			if (ps.getDuration() - executed > deadline.getTime()
//					- now.getTime())
//				return false;
//		}
//
//		return true;
//	}
//
//	private boolean violatesProhibitions(PlanSeq ps, APLModule module) {
//
//		ArrayList<Prohibition> hpp = module.getProhibitionbase().getHigher(
//				ps.getPriority());
//
//		if (hpp != null) {
//			for (Prohibition p : hpp) {
//				try {
//					if (existIn(ps, p, module))
//						return true;
//				} catch (NoRuleException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		return false;
//	}
//
//	private boolean existIn(PlanSeq ps, Prohibition pp, APLModule module)
//			throws NoRuleException {
//
//		for (Plan plan : ps.getPlans()) {
//			// plan.evaluateArguments();
//			if (plan instanceof BeliefUpdateAction) {
//				APLFunction p = ((BeliefUpdateAction) plan).getPlan();
//				SubstList<Term> theta = new SubstList<Term>();
//				Beliefbase beliefbase = module.getBeliefbase();
//				BeliefUpdate c;
//				c = module.getBeliefUpdates().selectBeliefUpdate(p, beliefbase,
//						theta);
//
//				if (c == null)
//					continue;
//
//				for (Literal l : c.getPost()) {
//					Literal lcopy = l.clone();
//					lcopy.applySubstitution(theta);
//					Literal pl = pp.getProhibition();
//					// System.out.println(lcopy.toString() +"  "+pl.toString());
//					if (lcopy.equals(pl))
//						return true;
//				}
//			}
//		}
//		return false;
//	}*/
//
//	private boolean passNorms(PlanSeq ps, APLModule module) {
//
//
//		if (violatesProhibitions(ps, module))
//			return false;
//
//
//		Date deadline = ps.getDeadline();
//		if (deadline == null || deadline.getTime() == Long.MAX_VALUE)
//			return true;
//
//		Date started = ps.getExecStart();
//		Date now = new Date();
//		if (started == null) {
//			if (ps.getDuration() + now.getTime() > deadline.getTime())
//				return false;
//		} else {
//			long executed = now.getTime() - started.getTime();
//			if (ps.getDuration() - executed > deadline.getTime()
//					- now.getTime())
//				return false;
//		}
//
//		return true;
//	}
//
//	private boolean violatesProhibitions(PlanSeq ps, APLModule module) {
//
//		ArrayList<Prohibition> hpp = module.getProhibitionbase().getHigher(ps.getPriority());
//		//System.out.println("[Prohibitions]:"+ hpp.toString());
//		if (hpp != null) {
//			for (Prohibition p : hpp) {
//				try {
//					if (existIn(ps, p, module))
//						return true;
//				} catch (NoRuleException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		return false;
//	}
//
//	private boolean existIn(PlanSeq ps, Prohibition pp, APLModule module) throws NoRuleException {
//
//		for (Plan plan : ps.getPlans()) {
//
//			if (plan instanceof BeliefUpdateAction) {
//				APLFunction p = ((BeliefUpdateAction) plan).getPlan();
//
//				SubstList<Term> theta = new SubstList<Term>();
//				Beliefbase beliefbase = module.getBeliefbase().clone();
//				LinkedList<Literal> pl = pp.getProhibitions();
//
//				if (!beliefbase.doGoalQuery(new Goal(pl), theta)) {
//
//					BeliefUpdate c;
//					c = module.getBeliefUpdates().selectBeliefUpdate(p, beliefbase,
//							theta);
//
//					if (c == null)
//						continue;
//
//					for (Literal l : c.getPost()) {
//						Literal lcopy = l.clone();
//						lcopy.applySubstitution(theta);
//
//						beliefbase.assertBelief(lcopy);
//						//System.out.println("post: " + lcopy.toString());
//
//					}
//
//					//	System.out.println("beliefbase: " + beliefbase.toString());
//					//	System.out.println("prohibition: " + pl.toString());
//					if (beliefbase.doGoalQuery(new Goal(pl), theta))
//						System.out.println("!!prohibited: " + pl.toString() + " plan: "+ plan);
//					//System.out.println("!!prohibited: " + pl.toString());
//					return true;
//				}
//				else return false;
//			}
//		}
//		return false;
//	}
//
//}
//
// */
// 
