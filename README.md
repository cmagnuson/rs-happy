# rs-happy
Streaming times from MyLaps Timing & Scoring to RunScore can cause RunScore to crash, or can be very slow when dealing with a large volume of times.

This tool receives the MyLaps stream and converts it to a RunScore Open feed, which RunScore is much more happy to handle - much faster and seemingly no crashes.

By default it listens on port `3097` for a MyLaps feed, and sends a RunScore Open feed on port `4001`. 

More detail on how location mapping is handled in `doc/README.md`

This can be built with `./gradlew distZip createExe` or a release downloaded from releases page.