# Buoy - Safely Navigating Panama

The JDK's up-and-coming [Project Panama](https://openjdk.org/projects/panama/)
Foreign Function & Memory API provides a very low-level and explicit
API that very directly maps native code concepts into a Java API.
While this is ideal for providing the level of control necessary
to stably bind to native code, it is not ideal for hand-writing
bindings to native code and existing tools for automating binding
creation are less than ideal for some scenarios. Buoy provides a
small, simple, and very thin runtime wrapper over Panama's verbose
and subtle parts to make hand-writing native bindings faster, easier,
and less error-prone than other alternatives.

Buoy fills a role virtually identical to .NET's P/Invoke and is somewhat
similar in usage, though any API similarities are entirely coincidental
as Buoy makes no attempt to imitate P/Invoke's API or usage patterns
in any capacity.

## Why not hand-write Panama bindings directly?
Issues with writing Panama code directly, from a high level perspective:
* Verbose code, tedious and time-consuming to type.
* Lots of opportunities for subtle inconsistencies and errors that
  are well-hidden and hard to find.
* Difficult to maintain, especially for larger native libraries.

## Automatic binding creation with Jextract
Panama has an experimental tool, [jextract](https://github.com/openjdk/jextract), that parses C headers
and automatically produces bindings corresponding to those headers
for the platform it's running on. `jextract` is a powerful tool and
is a huge step forward over hand-written Panama bindings, but it has
its own limitations in certain scenarios (particularly libraries 
that are cross-platform but may leak platform API details into the 
user-facing API):
* You must run `jextract` as part of your build process, and depending
  on how complex the extraction process is for your use-case this can
  potentially get a bit messy.
* Bindings produced by `jextract` are specific to the platform it runs
  on. This means that producing cross-platform bindings probably involves
  either running `jextract` on each of your intended runtime platforms
  and then hand-writing a facade that separately calls into the 
  platform-specific Panama bindings, or detailed knowledge and a carefully
  constructed set of filters to separate out the platform-specific parts of
  the native libraries into a small subset of the overall binding.
* `jextract`-generated bindings tend to be a bit messy, especially
  anything that deals with struct fields.

## Buoy - safely navigating Panama code
For some projects `jextract` is the best option, but for others it
may not be. That's where Buoy comes in. Buoy provides a concise, 
declarative API for modeling native structs and functions and binding
to them, and it supports declaring structs and functions via annotations
on Java classes. Buoy's mapper provides powerful facilities for automatically
accessing native functions, struct fields, and pointers, and wrapping them
in higher-level Java classes. It makes no effort to hide or limit access
to Panama features in any way, but it does provide simple and clear shorthand
options for most common tasks. It interacts cleanly with Java's polymorphism,
allowing use of inheritance and interfaces to model struct nesting and to
abstract platform-specific features away from user-facing code. Buoy also
provides some facilities to make finding and loading native libraries more pleasant,
such as OS/Architecture detection and generating library file names according to
standard platform conventions.

Buoy's advantages:
* Runtime only, very little overhead for populating bound objects
  and no overhead at invocation time.
* It takes very little code to access a couple of functions from
  a platform API (e.g., enabling interactive mode in a Windows terminal).
* Extremely concise, clean, and declarative binding declarations.
* Imperative or annotation-based binding declaration, both options
  are fully interoperable with each other.
* It's easy and safe to hide platform-specific native APIs behind interfaces.
* Concise, clean, and easy to use shorthand options for common
  operations that may be verbose with vanilla Panama (such as 
  dereferencing a pointer).
* No hiding - at any point you can drop down to directly writing
  vanilla Panama code with no "gotchas" or magical surprises
  popping up.

Buoy's disadvantages:
* You still have to hand-write and maintain binding code. 
  For large API surfaces this may not be desirable.
  

Note that Panama is currently a preview feature of the JDK and
changes with every Java release. You should consider Buoy to be
of the same status for now - it may slightly lag JDK releases
as it takes time to accommodate Panama API changes, wait for Gradle
to support the newest JDK releases, etc. Buoy's API is also undergoing
its own evolution right now, and will not stabilize until Panama does
(at the soonest).

## Project Structure
Buoy itself resides in [lib/](./lib), and a small, simple example native
library that's used for testing resides in [native/](./native). The Buoy
bindings for the native test library live 
[here](./lib/src/test/groovy/com/myworldvw/buoy/util) and provide a fairly
complete example of Buoy's mapping features and some of its utility features
as well.