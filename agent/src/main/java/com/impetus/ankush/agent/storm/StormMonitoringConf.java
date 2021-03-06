/*******************************************************************************
 * ===========================================================
 * Ankush : Big Data Cluster Management Solution
 * ===========================================================
 * 
 * (C) Copyright 2014, by Impetus Technologies
 * 
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License (LGPL v3) as
 * published by the Free Software Foundation;
 * 
 * This software is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this software; if not, write to the Free Software Foundation, 
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 ******************************************************************************/
/**
 * 
 */
package com.impetus.ankush.agent.storm;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author mayur
 * 
 */
public class StormMonitoringConf {

	private String nimbusConf;

	private int nimbusUpTime;

	private int supervisorSize;

	private int topologySize;

	private List<Supervisor> supervisors;

	private List<Topology> topologies;

	private int usedSlots = 0;

	private int freeSlots = 0;

	private int totalSlots = 0;

	private int executors = 0;

	private int tasks = 0;

	/**
	 * @return the nimbusConf
	 */
	public String getNimbusConf() {
		return nimbusConf;
	}

	/**
	 * @param nimbusConf
	 *            the nimbusConf to set
	 */
	public void setNimbusConf(String nimbusConf) {
		this.nimbusConf = nimbusConf;
	}

	/**
	 * @return the nimbusUpTime
	 */
	public int getNimbusUpTime() {
		return nimbusUpTime;
	}

	/**
	 * @param nimbusUpTime
	 *            the nimbusUpTime to set
	 */
	public void setNimbusUpTime(int nimbusUpTime) {
		this.nimbusUpTime = nimbusUpTime;
	}

	/**
	 * @return the supervisorSize
	 */
	public int getSupervisorSize() {
		return supervisorSize;
	}

	/**
	 * @param supervisorSize
	 *            the supervisorSize to set
	 */
	public void setSupervisorSize(int supervisorSize) {
		this.supervisorSize = supervisorSize;
	}

	/**
	 * @return the topologySize
	 */
	public int getTopologySize() {
		return topologySize;
	}

	/**
	 * @param topologySize
	 *            the topologySize to set
	 */
	public void setTopologySize(int topologySize) {
		this.topologySize = topologySize;
	}

	/**
	 * @return the supervisors
	 */
	public List<Supervisor> getSupervisors() {
		return supervisors;
	}

	/**
	 * @param supervisors
	 *            the supervisors to set
	 */
	public void setSupervisors(List<Supervisor> supervisors) {
		this.supervisors = supervisors;
	}

	/**
	 * @return the topologies
	 */
	public List<Topology> getTopologies() {
		return topologies;
	}

	/**
	 * @param topologies
	 *            the topologies to set
	 */
	public void setTopologies(List<Topology> topologies) {
		this.topologies = topologies;
	}

	/**
	 * 
	 */
	public void calculateMetrics() {
		// calculate free slots..
		this.freeSlots = this.totalSlots - this.usedSlots;

		// iterate over each topology in rawtopolgoydata
		ObjectMapper mapper = new ObjectMapper();
		for (Topology topology : this.getTopologies()) {
			Map rawData = topology.getRawTopologyData();

			for (Object component : rawData.keySet()) {
				Object obj = ((List) rawData.get(component)).get(0);
				Map map = (Map) obj;
				if (map.get("type").equals("bolt")) {
					List<Bolt> partialBolts = mapper.convertValue(
							rawData.get(component),
							new TypeReference<List<Bolt>>() {
							});
					Bolt bolt = getBolt(partialBolts);
					bolt.setId(component.toString());
					topology.addBolt(bolt);
				} else {
					List<Spout> partialSpouts = mapper.convertValue(
							rawData.get(component),
							new TypeReference<List<Spout>>() {
							});
					Spout spout = getSpout(partialSpouts);
					spout.setId(component.toString());
					topology.addSpout(spout);
				}
			}
		}
	}

	public Bolt getBolt(List<Bolt> bolts) {
		Bolt bolt = new Bolt();
		for (Bolt newBolt : bolts) {
			bolt.setEmitted(bolt.getEmitted() + newBolt.getEmitted());
			bolt.setAcked(bolt.getAcked() + newBolt.getAcked());
			bolt.setTransferred(bolt.getTransferred()
					+ newBolt.getTransferred());
			bolt.setFailed(bolt.getFailed() + newBolt.getFailed());
			bolt.setExecuteLatency(bolt.getExecuteLatency()
					+ newBolt.getExecuteLatency() / bolts.size());
			bolt.setProcessLatency(bolt.getProcessLatency()
					+ newBolt.getProcessLatency() / bolts.size());
			bolt.setParallelism(newBolt.getParallelism());
			bolt.setLastError(newBolt.getLastError());
			bolt.setExecuted(bolt.getExecuted() + newBolt.getExecuted());
		}
		bolt.setType("bolt");
		bolt.setNumberOfExecutors(bolts.size());
		bolt.setNumberOfTasks(bolts.size());

		return bolt;
	}

	public Spout getSpout(List<Spout> spouts) {
		Spout spout = new Spout();
		for (Spout newSpout : spouts) {
			spout.setEmitted(spout.getEmitted() + newSpout.getEmitted());
			spout.setAcked(spout.getAcked() + newSpout.getAcked());
			spout.setTransferred(spout.getTransferred()
					+ newSpout.getTransferred());
			spout.setFailed(spout.getFailed() + newSpout.getFailed());
			spout.setCompleteLatency(spout.getCompleteLatency()
					+ newSpout.getCompleteLatency() / spouts.size());
			spout.setParallelism(newSpout.getParallelism());
			spout.setLastError(newSpout.getLastError());
		}
		spout.setType("spout");
		spout.setNumberOfExecutors(spouts.size());
		spout.setNumberOfTasks(spouts.size());

		return spout;
	}

	/**
	 * @return the usedSlots
	 */
	public int getUsedSlots() {
		return usedSlots;
	}

	/**
	 * @param usedSlots
	 *            the usedSlots to set
	 */
	public void setUsedSlots(int usedSlots) {
		this.usedSlots = usedSlots;
	}

	/**
	 * @return the freeSlots
	 */
	public int getFreeSlots() {
		return freeSlots;
	}

	/**
	 * @param freeSlots
	 *            the freeSlots to set
	 */
	public void setFreeSlots(int freeSlots) {
		this.freeSlots = freeSlots;
	}

	/**
	 * @return the totalSlots
	 */
	public int getTotalSlots() {
		return totalSlots;
	}

	/**
	 * @param totalSlots
	 *            the totalSlots to set
	 */
	public void setTotalSlots(int totalSlots) {
		this.totalSlots = totalSlots;
	}

	/**
	 * @return the executors
	 */
	public int getExecutors() {
		return executors;
	}

	/**
	 * @param executors
	 *            the executors to set
	 */
	public void setExecutors(int executors) {
		this.executors = executors;
	}

	/**
	 * @return the tasks
	 */
	public int getTasks() {
		return tasks;
	}

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(int tasks) {
		this.tasks = tasks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StormMonitoringConf [nimbusConf=");
		builder.append(nimbusConf);
		builder.append(", nimbusUpTime=");
		builder.append(nimbusUpTime);
		builder.append(", supervisorSize=");
		builder.append(supervisorSize);
		builder.append(", topologySize=");
		builder.append(topologySize);
		builder.append(", supervisors=");
		builder.append(supervisors);
		builder.append(", topologies=");
		builder.append(topologies);
		builder.append(", usedSlots=");
		builder.append(usedSlots);
		builder.append(", freeSlots=");
		builder.append(freeSlots);
		builder.append(", totalSlots=");
		builder.append(totalSlots);
		builder.append(", executors=");
		builder.append(executors);
		builder.append(", tasks=");
		builder.append(tasks);
		builder.append("]");
		return builder.toString();
	}

}
