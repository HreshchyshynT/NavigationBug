package com.example.navigationbug

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.navigationbug.ui.main.MainFragment
import java.lang.IllegalStateException
/**
    STEPS TO REPRODUCE:
        1. Enable "Don't keep activities" option in developer's settings"
        2. Launch app: Fragment one is visible
        3. Move app to background by pressing "Home" button
        4. Open app again: Fragment app is still visible, but Fragment two should be visible (bool value is false) - BUG
 */
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    lateinit var navHost: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        log("fresh start = ${savedInstanceState == null}")
        navHost = supportFragmentManager.findFragmentById(R.id.main_nav_host) as NavHostFragment
        navController = navHost.navController
        createGraph(App.instance.getValue())
    }

    private fun createGraph(bool: Boolean) {
        log("one: ${R.id.fragment_one}, two: ${R.id.fragment_two}")
        val graph =
            if (bool) {
                log("fragment one")
                navController.navInflater.inflate(R.navigation.nav_graph).also {
                    it.startDestination = R.id.fragment_one
                }
            } else {
                log("fragment two")
                navController.navInflater.inflate(R.navigation.nav_graph).also {
                    it.startDestination = R.id.fragment_two
                }
            }
        navController.setGraph(graph, null)

        // it helps if we don't need to restore state
//        navController.restoreState(Bundle())
//        if (bool) {
//        navController.setGraph(graph, null)
//            log("graph set")
//        } else {
//        even if we don't set graph, user will see fragments from previous backStack
//            log("don't set graph")
//        }
        /* FIXME: fixing first bug, creating new one
        if current destination after setting new graph is not the same as graph.startDestination
        then previous backstack was restored this solution will help and we will see desired start destination,
        but navHost still keeps previous start destination and display it under current fragment
        and if user press Back button, current start destination will be removed and previous one will be visible
        STEPS TO REPRODUCE:
        1. Enable "Don't keep activities" option in developer's settings"
        2. Launch an app: Fragment one is visible
        3. Move app to background by pressing "Home" button
        4. Open app again: Fragment two is visible
        5. Press "Back" button: fragment one is visible again - BUG
         */
        //FIXME: uncomment from here
//        if (navController.currentDestination?.id != graph.startDestination) {
//            while (!navController.backStack.isEmpty()) {
//                navController.popBackStack()
//            }
//            navController.setGraph(graph, null)
//
//            log("Current entity after resetting: ${navController.currentDestination?.id}")
//        } else {
//            log("navController.currentDestination?.id == graph.startDestination")
//        }
    }


    private fun log(text: String) {
        Log.d("TESTING", text)
    }
}