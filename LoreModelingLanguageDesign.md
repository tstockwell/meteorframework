# Abstract #
The motivation for creating the 'Lore' programming language and it's technical underpinnings are described.
Lore is high level language for creating executable models.
Lore can be used for high-level programming or for modeling software systems.
Lore programs are written in an extended JavaScript-like syntax which can be directly translated to RDF.
All Lore modeling and programming concepts, like algorithms and class hierarchies, are formalized as RDF resources.
Lore has an executable model based on the Abstract State Machine method.
Lore models can extend or change other Lore models.

# Introduction #
Lore is a tool that can be used by programmers to model aspects of a software system without being bound to a particular
programming language or framework.
Lore can also be used as a high-level programming language by itself.
Lore models can be combined to create larger models.
Lore models can extend or change other Lore models, thus software systems designed with Lore can be extended, changed, and customized without access to the underlying code that executes the models.
Lore provides modeling facilites for applying extensions and changes to other models in sophisticated ways.

## Motivation ##
Software modeling is a way to express what a software system should do without becoming mired in implementation details.
The Resource Description Framework (RDF) is a metadata model that is used as a general method for conceptual description or modeling of information, using a variety of syntax formats.
However, current XML and textual RDF syntaxes are cumbersome and unfamiliar to most programmers.
Lore is meant to be a syntax that will be generally familiar to programmers and also capable of easily expressing
the concepts found in a typical high-level programming language such as algorithms, class hierarchies, and object instances.
Lore provides a basic model of computation, modeled in RDF, and an expressive syntax that programmers will find familiar.
The Lore toolkit can translate specifications written in the Lore language into RDF/XML which can be reused and executed by other software systems.

## Executable Model ##
A software model should be capable of describing a software system's data structures as well as it behavior.
Like other programming languages, in order to be able to describe behavior Lore needs an underlying model of computation.
For instance, LISP is based on lambda calculus, Erlang is based on the Actor Model, and Java is based on a virtual machine specification.

Lore's model of computation is based on Abstract State Machines (ASM).
ASMs will not be explained here, only an explanation of why ASMs were chosen as a foundation for Lore is explained.
Abstract State Machines have the advantage of being able to model an algorithm at arbitrary levels of abstraction.
ASMs can provide high-level, low-level and mid-level views of a hardware or software design, this
makes ASMs well suited for use when we wish to avoid entanglement with details that are not
of interest to the domain that we are modeling.

ASMs also have a basic, built-in, notion of concurrency.
When modeling with ASMs the order in which tasks are executed is not specified unless it is important to the domain being modeled.  Thus, a runtime engine that executes a Lore specification may execute the specification as efficiently as possible.

## Model Customization ##
All software systems must change.
In fact, making changes to a software system is frequently more expensive and time consuming than building the system in the first place.
Lore is not exclusively focused on describing a software system, Lore is also focused on describing changes and customizations   to an existing system.
This makes Lore especially unique among modeling and programming languages.

### Open World Assumption ###
RDF provides the basic facility for creating extensible models. RDF assumes what is called the 'Open World Assumption', that is, any RDF model can make statements about any resource defined in any other RDF model.  For instance, an RDF model can add a property to a class defined in another RDF model.  Lore inherits this 'Open World' capability from RDF.


### Contextual Programming ###
RDF provides Lore with a basic ability to apply structural changes to other models.
Lore's programming model defines the concepts necessary for a model to apply behavioral changes to other models.
Lore defines concepts such as Interceptors, Multi-Methods, Providers, and Scopes, that provide the ability to apply behavioral changes to other models.  Also, changes can be applied that only apply when the model is executed in a certain context, for instance, when the model is run in a test environment versus a production environment, or when a certain user is logged in.

### Books ###
Lore models are packaged into modules called Books.  Books may be dependent on other books.  Thus, Books form a hierarchy of dependendent books, the Books ate the lowest levels of the hierarchy have no dependencies, Books at the hight level of the hierarchy have the most dependencies.  The models inside a Book may only apply changes to models on which it is dependent that are naturally lower in the hierarchy.
A Lore system is a collection of Books.
A system may be extended or customized by adding or removing Books from the system, thus automatically changing the runtime structure and behavior of the system.


## The Name ##
The concept for Lore was inpired by Fabl, another programming language that can be translated to RDF.






# Basic Design Goals #

  * As familiar as possible to Java/C# programmers.
    * Case sensitive.
    * ; may be used to terminate lines
    * // and // for comments
  * Meteor execution model is based on principles of Abstract State Machines.
    * Modeling with ASMs allows us to specify only what is needed and no more.
    * ASM model is concurrent except where specified in the model.
  * Meteor data model is based on semantic modeling principles (RDF/OWL).
    * Semantic modeling avoids ambiguity of using labels to identify properties like in most OO languages.

## Notable Differences between Meteorscript and Java ##
  * Statements are executed concurrently. 'step' blocks are used to define sequential tasks.

# Types #

Meteorscript supports the following native types:
  * Array
  * Number
  * Boolean
  * Object
  * DateTime
  * String

