/* $Id: 4cd06794959ca7bd59432990ae8f934d02b9e26d $
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
package nl.rivm.cib.episim.model.scenario;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.measure.unit.NonSI;
import javax.measure.unit.UnitFormat;

import org.apache.logging.log4j.Logger;
import org.junit.Test;

import io.coala.log.LogUtil;
import io.coala.time.x.Instant;
import nl.rivm.cib.episim.time.Scheduler;
import nl.rivm.cib.episim.time.dsol3.Dsol3Scheduler;
import nl.rivm.cib.episim.util.Caller;

/**
 * {@link HouseholdTest}
 * 
 * @version $Id: 4cd06794959ca7bd59432990ae8f934d02b9e26d $
 * @author Rick van Krevelen
 */
public class HouseholdTest
{
	/** */
	static final Logger LOG = LogUtil.getLogger( HouseholdTest.class );
	{
		UnitFormat.getInstance().alias( NonSI.DAY, "days" );
		//UnitFormat.getInstance().label(DAILY, "daily");
	}

	/**
	 * This test should:
	 * <ol>
	 * </ol>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void householdCompositionTest() throws Throwable
	{
		LOG.trace( "Initializing household composition scenario..." );

		final Scheduler scheduler = Dsol3Scheduler.of( "dsol3Test",
				Instant.of( "0 days" ),
				io.coala.time.x.Duration.of( "100 days" ),
				Caller.rethrow( new Geard2011Scenario()::init ) );

		LOG.trace( "Starting household composition scenario..." );
		final CountDownLatch latch = new CountDownLatch( 1 );
		scheduler.time().subscribe( ( t ) ->
		{
			LOG.trace( "t = {}", t.prettify( NonSI.DAY, 1 ) );
		}, ( e ) ->
		{
			LOG.warn( "Problem in scheduler", e );
		}, () ->
		{
			latch.countDown();
		} );
		scheduler.resume();
		latch.await( 3, TimeUnit.SECONDS );
		assertEquals( "Should have completed", 0, latch.getCount() );
	}

}
