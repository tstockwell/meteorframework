# Groovy instead of Java #

One major problem with using Java for modeling is that you can only declare one type of object in Java source files - classes.
However, Groovy allows for declaring data as well as code in source files - Groovy makes a better language to start with.

## 'native' multimethods ##
Groovy has built-in support for multimethods.
This could make Meteor's runtime implementation much simpler.

## constructor support ##
Use Groovy Metaclass to implement runtime behavior instead of Java proxy approach - the Groovy approach supports constructors.

## Property and method literals ##
Also, using Groovy's AST builder it is possible to provide better support for method and property literals.
Eliminates need for generated APT code.

# Capabilities #

A Capability is an implementation of some specific aspect of the runtime system.  Persistence, user interface construction, transaction management, and remoting, are examples of capabilities.
Capabilities are system implementation aspects.

Modules should specify the capabilities that they require and any capabilities that they provide.

When an API from a modules that requires a capability is used by some other module then the required capability must be present at runtime in order to execute the model.

