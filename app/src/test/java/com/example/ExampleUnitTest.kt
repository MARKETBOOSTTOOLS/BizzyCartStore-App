package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun initialData_containsRequiredCategoriesAndProducts() {
    val categories = com.example.data.initial.InitialData.categories
    val products = com.example.data.initial.InitialData.products
    val coupons = com.example.data.initial.InitialData.coupons

    assertTrue("Categories should have at least 10 entries", categories.size >= 10)
    assertTrue("Products should have at least 20 entries", products.size >= 20)
    assertTrue("Coupons should have WELCOME10", coupons.any { it.code == "WELCOME10" })

    // Verify all products have valid pricing in INR
    products.forEach { p ->
      assertTrue("Product price should be positive", p.price > 0)
      assertTrue("Product MRP should be >= price", p.mrp >= p.price)
      assertTrue("Product name should not be empty", p.name.isNotBlank())
    }
  }
}
