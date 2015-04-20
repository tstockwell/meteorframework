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
