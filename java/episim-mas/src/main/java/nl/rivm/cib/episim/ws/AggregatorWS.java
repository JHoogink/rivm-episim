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
package nl.rivm.cib.episim.ws;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import io.coala.json.JsonUtil;
import nl.rivm.cib.episim.api.AggregatorAPI;

/**
 * {@link AggregatorWS} exposes the statistics aggregation function provided by
 * some {@link AggregatorAPI} instance in a web service
 * 
 * @version $Id$
 * @author Rick van Krevelen
 */
@Path( "/stat" )
public class AggregatorWS
{

	private AggregatorAPI aggregator = null;

	/**
	 * welcome page
	 *
	 * @return the current aggregates in JSON format
	 */
	@GET
	@Produces( MediaType.TEXT_HTML )
	public Response welcome()
	{
		return Response
				.ok( "<html><head><title>EPISIM - Stats</title></head>"
						+ "<body><p>Welcome to EPISIM - statistics</p></body></html>" )
				.build();
	}

	/**
	 * welcome page
	 *
	 * @return the current aggregates in JSON format
	 */
	@GET
	@Path( "/fact" )
	@Produces( MediaType.APPLICATION_JSON )
	public Response selectFacts()
	{
		final JsonNode query = JsonUtil.getJOM().createObjectNode();
		final JsonNode response = this.aggregator.select( query );
		return Response.ok( JsonUtil.toString( response ) ).build();
	}

	/**
	 * add statistical values for aggregation
	 *
	 * @param json the statistical values to aggregate in JSON format
	 * @return the response code in JSON format
	 */
	@PUT
	@Path( "/fact" )
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public Response insertFact( final InputStream json )
	{
		final JsonNode statistics = JsonUtil.toTree( json );
		final JsonNode response = this.aggregator.insert( statistics );
		return Response.ok( JsonUtil.toString( response ) ).build();
	}

}
