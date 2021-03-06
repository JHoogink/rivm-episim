/* $Id: c497843c72fe02d3fb4c20067d0832b27514147b $
 * 
 * Part of ZonMW project no. 50-53000-98-156
 * 
 * @license
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Copyright (c) 2016 RIVM National Institute for Health and Environment 
 */
package nl.rivm.cib.episim.model.impl;

import static io.coala.random.RandomDistribution.Util.asAmount;

import javax.inject.Inject;
import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;

import org.jscience.physics.amount.Amount;

import io.coala.bind.Binder;
import io.coala.random.RandomDistribution;
import io.coala.random.RandomNumberStream;
import io.coala.time.x.TimeSpan;
import nl.rivm.cib.episim.model.Condition;
import nl.rivm.cib.episim.model.EpidemicCompartment;
import nl.rivm.cib.episim.model.Infection;
import nl.rivm.cib.episim.model.SymptomPhase;
import nl.rivm.cib.episim.model.TransitionEvent;
import nl.rivm.cib.episim.model.TransmissionEvent;
import nl.rivm.cib.episim.model.TreatmentStage;
import nl.rivm.cib.episim.time.Timed;

/**
 * {@link HIB} is an invasive disease caused by the Hib bacteria residing in
 * nose-throat cavity. It occasionally causes blood poisoning (sepsis), septic
 * arthritis, pneumonia, epiglottis and meningitis. It has a
 * <a href="http://rijksvaccinatieprogramma.nl/De_ziekten/HIb">RVP page</a>, a
 * <a href="http://www.rivm.nl/Onderwerpen/H/Haemophilus_influenzae_type_b">RIVM
 * page</a>, a <a href="http://www.cdc.gov/hi-disease/">US CDC page</a>, and a
 * <a href="http://www.who.int/immunization/topics/hib/en/">WHO immunization
 * page</a>
 * 
 * @version $Id: c497843c72fe02d3fb4c20067d0832b27514147b $
 * @author Rick van Krevelen
 */
public abstract class HIB implements Infection, Timed
{

//	@SuppressWarnings( "unchecked" )
//	private static final Map<TransmissionRoute, Amount<Dimensionless>> ROUTE_LIKELIHOODS = map(
//			entry( TransmissionRoute.DROPLET, Amount.ONE ),
//			entry( TransmissionRoute.DIRECT, Amount.ONE ),
//			entry( TransmissionRoute.ORAL, Amount.ONE ) );

	private RandomDistribution<Amount<Duration>> onsetPeriodDist;

	private RandomDistribution<Amount<Duration>> symptomPeriodDist;

	private RandomDistribution<Amount<Duration>> seroconversionPeriodDist;

	private RandomDistribution<Amount<Duration>> latentPeriodDist;

	private RandomDistribution<Amount<Duration>> recoverPeriodDist;

	private RandomDistribution<Amount<Duration>> wanePeriodDist;

	@Inject
	public HIB( final Binder binder )
	{
		final RandomNumberStream rng = binder
				.inject( RandomNumberStream.class );
		final RandomDistribution.Factory rdf = binder
				.inject( RandomDistribution.Factory.class );
		this.seroconversionPeriodDist = asAmount( rdf.getConstant( 28 ),
				NonSI.DAY );
		this.latentPeriodDist = asAmount( rdf.getConstant( 1 ), NonSI.HOUR );
		this.recoverPeriodDist = asAmount( rdf.getConstant( 100 ), NonSI.YEAR );
		this.wanePeriodDist = asAmount( rdf.getConstant( 0 ), NonSI.DAY );
		this.onsetPeriodDist = asAmount( rdf.getTriangular( rng, 1, 6, 30 ),
				NonSI.MONTH );
	}

	public void invade( final TransmissionEvent transmission )
	{
		after( TimeSpan.ZERO ).call( HIB::expose, this,
				transmission.getSecondaryCondition() );
	}

	public static void expose( final HIB self, final Condition condition )
	{
//		transitions.onNext(
//				TransitionEvent.of( condition, EpidemicCompartment.Simple.EXPOSED ) );
		self.after( TimeSpan.of( self.seroconversionPeriodDist.draw() ) )
				.call( HIB::infect, self, condition );
		self.after( TimeSpan.of( self.latentPeriodDist.draw() ) )
				.call( HIB::infect, self, condition );
		self.after( TimeSpan.of( self.onsetPeriodDist.draw() ) )
				.call( HIB::infect, self, condition );
	}

	public static void symptomize( final HIB self, final Condition condition )
	{
//		transitions.onNext(
//				TransitionEvent.of( condition, SymptomPhase.SYSTEMIC ) );
		self.after( TimeSpan.of( self.symptomPeriodDist.draw() ) )
				.call( HIB::asymptomize, self, condition );
	}

	public static void asymptomize( final HIB self, final Condition condition )
	{
//		transitions.onNext(
//				TransitionEvent.of( condition, SymptomPhase.ASYMPTOMATIC ) );
	}

	public static void seroconvert( final HIB self, final Condition condition )
	{
//		transitions.onNext( TransitionEvent.of( condition,
//				TreatmentStage.PRE_EXPOSURE_PROPHYLACTIC ) );
	}

	public static void infect( final HIB self, final Condition condition )
	{
//		transitions.onNext( TransitionEvent.of( condition,
//				EpidemicCompartment.Simple.INFECTIVE ) );
		self.after( TimeSpan.of( self.recoverPeriodDist.draw() ) )
				.call( HIB::recover, self, condition );
	}

	public static void recover( final HIB self, final Condition condition )
	{
//		transitions.onNext( TransitionEvent.of( condition,
//				EpidemicCompartment.Simple.RECOVERED ) );
		self.after( TimeSpan.of( self.wanePeriodDist.draw() ) ).call( HIB::wane,
				self, condition );
	}

	public static void wane( final HIB self, final Condition condition )
	{
//		transitions.onNext( TransitionEvent.of( condition,
//				EpidemicCompartment.Simple.SUSCEPTIBLE ) );
	}
}