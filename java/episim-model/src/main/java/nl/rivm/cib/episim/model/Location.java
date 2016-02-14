/* $Id$
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
package nl.rivm.cib.episim.model;

import java.util.Collection;

import org.opengis.spatialschema.geometry.geometry.Position;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * {@link Location}
 * 
 * @version $Id$
 * @author Rick van Krevelen
 */
public interface Location
{

	/**
	 * {@link Carrier}s arriving at or departing from this {@link Location} may
	 * cause it to generate {@link ContactEvent}s or add/remove {@link Route}s
	 * (e.g. contaminated objects, food, water, blood, ...)
	 * 
	 * @param event a {@link TravelEvent} with either (
	 *            {@link TravelEvent#getDestination()} == this {@link Location}
	 *            and {@link TravelEvent#getArrival()} == now) or (
	 *            {@link TravelEvent#getOrigin()} == this {@link Location} and
	 *            {@link TravelEvent#getDeparture()} == now)
	 */
	void on( TravelEvent event );

	/** @return the global {@link Position} of this {@link Location} */
	Position getPosition();

	/** @return the current {@link Collection} of transmission {@link Route}s */
	Collection<Route> getRoutes();

	/** @return the current {@link Collection} of {@link Carrier} occupants */
	Collection<Carrier> getOccupants();

	/**
	 * @return an {@link Observable} stream of {@link ContactEvent}s generated
	 *         by {@link Carrier} occupants of this {@link Location}
	 */
	Observable<ContactEvent> emitContacts();

	/**
	 * @return an {@link Observable} stream of {@link TransmissionEvent}s
	 *         generated by {@link Carrier} occupants of this {@link Location}
	 */
	Observable<TransmissionEvent> emitTransmissions();

	/**
	 * {@link SimpleInfection} is a {@link Infection} and {@link Observer} of
	 * {@link ContactEvent}s which in turn may trigger its transmission by
	 * generating {@link TransmissionEvent}s.
	 * 
	 * 
	 * @version $Id$
	 * @author Rick van Krevelen
	 */
	public abstract class SimpleLocation implements Location
	{

		private Subject<ContactEvent, ContactEvent> contact = PublishSubject
				.create();

		private Subject<TransmissionEvent, TransmissionEvent> transmission = PublishSubject
				.create();

		@Override
		public Observable<ContactEvent> emitContacts()
		{
			return this.contact.asObservable();
		}

		@Override
		public Observable<TransmissionEvent> emitTransmissions()
		{
			return this.transmission.asObservable();
		}

		public void subscribeTo( final Individual traveler )
		{
			final Location here = this;
			traveler.getTravels().filter( new Func1<TravelEvent, Boolean>()
			{
				@Override
				public Boolean call( final TravelEvent travel )
				{
					return travel.getArrival().equals( here )
							|| travel.getDeparture().equals( here );
				}
			} ).subscribe( new Action1<TravelEvent>()
			{
				@Override
				public void call( final TravelEvent contact )
				{
//					for( Map.Entry<Carrier, Relation> susceptible : contact
//							.getSecondarySusceptibles().entrySet() )
//						if( transmit( contact.getLocation(), contact.getRoute(),
//								contact.getDuration(), susceptible.getValue() ) )
//						{
//							final Carrier exposed = susceptible.getKey();
//							this.transmission.onNext( TransmissionEvent
//									.valueOf( contact, contact.getOnset(), exposed ) );
//						}
				}
			} );
		}

	}

}