package com.pluralsight.orderfulfillment.order;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pluralsight.orderfulfillment.config.IntegrationConfig;
import com.pluralsight.orderfulfillment.test.TestIntegration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestIntegration.class, IntegrationConfig.class })		
public class NewWebsiteRouteOrderTest {
	
	@Inject
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setUp() throws Exception {
		// set DB reference items
		jdbcTemplate.execute("insert into orders.catalogitem (id, itemnumber, itemname, itemtype) "
				+ "values (1, '078-1344200444', 'Build your own JS framework in just 24hr', 'Book')");
		jdbcTemplate.execute("insert into orders.customer (id, firstname, lastname, email) "
				+ "values (1, 'Larry', 'Horse', 'larry@hello.com')"); 
	}

	@After
	public void tearDown() throws Exception {
		// wipe DB
		jdbcTemplate.execute("delete from orders.orderitem");
		jdbcTemplate.execute("delete from orders.\"order\"");
		jdbcTemplate.execute("delete from orders.catalogitem");
		jdbcTemplate.execute("delete from orders.customer");
	}

	@Test
	public void test() throws InterruptedException {
		// add tested action "precondition"
		jdbcTemplate.execute("insert into orders.\"order\" (id, customer_id, orderNumber, timeorderplaced, lastupdate, status) "
				+ "values (1, 1, '1001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N')");
		jdbcTemplate.execute("insert into orders.orderitem (id, order_id, catalogitem_id, status, price, quantity, lastupdate) "
				+ "values (1, 1, 1, 'N', 20.00, 1, CURRENT_TIMESTAMP)");
		// sleep 5sec ensure sql polling has time to execute before test measures
		Thread.sleep(5000);

		int expected = 1;
		int actual = jdbcTemplate.queryForObject("SELECT count(id) FROM orders.\"order\" where status = 'P'", Integer.class);
		assertEquals(expected, actual);
	}

	
}
