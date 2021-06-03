# NavigationBug
I am setting navigation graph programmatically to be able to set start destination depends on some condition (for example, active session) and all works pretty well, but when 
I tested this with "Don't keep activities" option enabled I faced with the following bug.
When activity is just recreated and app calls methood NavController.setGraph, NavController forces restoring the Navigation back stack (from internal field mBackStackToRestore in onGraphCreated method) even if 
start destination is different than before so user see wrong fragment.

Steps to reproduce:
  1. Enable "Don't keep activities" option in developer settings
  2. Open the app, session is active (App.instance.getValue() returns true): app sets graph with start destination R.id.fragment_one, Fragment One is visible
  3. Put the app to background by pressing "Home" button
  4. Open the app again, session is not active (App.instance.getValue() returns false), activity is recreated: app sets graph graph with start destination R.id.fragment_two, but Fragment One is still visible

![](https://media.giphy.com/media/TvedI7cQKjTl9FD3eB/giphy.gif)


There is one workaround (marked with FIXME comment): we can add this code after setting graph
```Kotlin
  if (navController.currentDestination?.id != graph.startDestination) {
      while (!navController.backStack.isEmpty()) { // this part is not necessary 
          navController.popBackStack()
      }
      navController.setGraph(graph, null)

      log("Current entity after resetting: ${navController.currentDestination?.id}")
  } else {
      log("navController.currentDestination?.id == graph.startDestination")
  }
```
If current destination after setting new graph is not the same as graph.startDestination then previous backstack was restored. I supposed that if we will set graph again, we will have completely new graph with desired start destination.
This solution will help and we will see desired start destination, but navHost still keeps previous start destination (Fragment One) and display it under current fragment, so if user press Back button, current start destination will be removed and previous one will be visible
Steps to reproduce after FIXME was uncommented:
  1. Enable "Don't keep activities" option in developer's settings"
  2. Launch an app: Fragment one is visible
  3. Move app to background by pressing "Home" button
  4. Open app again: Fragment two is visible
  5. Press "Back" button: fragment one is visible again
  
![](https://media.giphy.com/media/HwQ8CBtLwSV7C6jSvh/giphy.gif)
