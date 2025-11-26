# Fantasy Premier League

![kotlin-version](https://img.shields.io/badge/kotlin-2.2.21-blue?logo=kotlin)

**Compose Multiplatform** project running on following
* Android 
* iOS 
* Desktop 

Also includes
* Kotlin Notebook
* MCP Server

It also currently makes use of the following Jetpack libraries
* ViewModel
* Navigation 3
* Room
* DataStore

Related posts:
* [Using Realm persistence library in a Kotlin Multiplatform project](https://johnoreilly.dev/posts/realm-kotlinmultiplatform/)
* [Using new Swift Async Algorithms package to close the gap on Combine](https://johnoreilly.dev/posts/swift-async-algorithms-combine/)
* [Displaying Charts on iOS, Android, and Desktop using Compose Multiplatform](https://johnoreilly.dev/posts/compose-multiplatform-chart/)
* [Using Jetpack Room in Kotlin Multiplatform shared code](https://johnoreilly.dev/posts/jetpack_room_kmp/)
* [Using Navigation 3 with Compose Multiplatform](https://johnoreilly.dev/posts/navigation3-cmp/)

## Screenshots

### Android
<img width="300" height="2400" alt="Screenshot_20251101_204058" src="https://github.com/user-attachments/assets/e75753e5-badb-4641-9b16-6cf23608d2ed" />


### iOS
<img width="300" height="2622" alt="Simulator Screenshot - iPhone 17 Pro - 2025-11-01 at 20 37 58" src="https://github.com/user-attachments/assets/8a94c51c-b087-407a-ad7e-8caf2c793bc8" />


### Desktop

<img width="912" height="888" alt="Screenshot 2025-11-01 at 20 38 24" src="https://github.com/user-attachments/assets/d9cbc228-7d1c-4b25-929b-d090ed99bf64" />



### Kotlin Notebook
<img width="916" alt="Screenshot 2024-04-06 at 11 03 15" src="https://github.com/joreilly/FantasyPremierLeague/assets/6302/37fef7a1-190d-4c14-acdd-5dafc11e8e30">

<img width="932" alt="Screenshot 2024-04-06 at 11 03 36" src="https://github.com/joreilly/FantasyPremierLeague/assets/6302/53cba0ea-1175-4349-ab1f-9aba4f8f0066">



**MCP Server**

The `mcp-server` module uses the [Kotlin MCP SDK](https://github.com/modelcontextprotocol/kotlin-sdk) to expose an MCP tools endpoint (returning player/fixture info) that
can for example be plugged in to Claude Desktop as shown below.  That module uses same KMP shared code.

<img width="1356" height="967" alt="Screenshot 2025-07-16 at 21 17 33" src="https://github.com/user-attachments/assets/73baa4d3-5f5d-4d6f-9e9f-1f9d601029ab" />



To integrate the MCP server with Claude Desktop for example you need to firstly run gradle `shadowJar` task and then select "Edit Config" under Developer Settings and add something
like the following (update with your path)


```
{
  "mcpServers": {
    "kotlin-peopleinspace": {
      "command": "java",
      "args": [
        "-jar",
        "/Users/john.oreilly/github/FantasyPremierLeague/mcp-server/build/libs/serverAll.jar",
        "--stdio"
      ]
    }
  }
}
```


## Full set of Kotlin Multiplatform/Compose/SwiftUI samples

*  PeopleInSpace (https://github.com/joreilly/PeopleInSpace)
*  GalwayBus (https://github.com/joreilly/GalwayBus)
*  Confetti (https://github.com/joreilly/Confetti)
*  BikeShare (https://github.com/joreilly/BikeShare)
*  FantasyPremierLeague (https://github.com/joreilly/FantasyPremierLeague)
*  ClimateTrace (https://github.com/joreilly/ClimateTraceKMP)
*  GeminiKMP (https://github.com/joreilly/GeminiKMP)
*  MortyComposeKMM (https://github.com/joreilly/MortyComposeKMM)
*  StarWars (https://github.com/joreilly/StarWars)
*  WordMasterKMP (https://github.com/joreilly/WordMasterKMP)
*  Chip-8 (https://github.com/joreilly/chip-8)



