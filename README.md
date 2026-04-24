# CSPMGraphSynthesis

This package synthesises, and verifies the deadlock-freedom of, CSP models via a graph-based approach. The outputs of operation are:
- Database.csv: a dataset of entries including
    - the CSP model,
    - the refinement property checked,
    - the outcome of the verification,
    - the counterexample,
    - a version of the counterexample with hidden events revealed,
    - a version with hidden events removed, and
    - the set of hidden events
- Error.csv: detailing all the cases in which the refinement failed or the CSP file was not successfully parsed
- CSP files: all CSP files generated are made available

Verification is undertaken through use of the FDR model checker. 

### CSP Operators 
The following CSP operators are featured within generated models, either by default or through use of a command line flag (clf)
- Rename (clf)
- Dot (clf)
- ?, !, $ (clf)
- Prefix
- Sequential Composition 
- External Choice
- Internal Choice
- Generalised Parallel
- Alphabetised Parallel
- Interleave
- Hide

## Team
[Holly Hendry](https://www.cs.york.ac.uk/people/?username=hrh): University of York  
[Pedro Ribeiro](https://www.cs.york.ac.uk/people/?username=pfr): University of York  
[Frank Soboczenski](https://www.cs.york.ac.uk/people/feynman): University of York  


## Installation Steps
1. Download this git package
2. Open java project in your chosen IDE
3. Compile project

OR

1. Download the most recent release of the package for use within the command line

## Requirements
- Java 21
- FDR model checker: [installation instructions can be found here](https://cocotec.io/fdr/manual/gui/getting_started.html#gui-installation)
  
## Dataset Generation
### Execution parameters
- r: [optional] (boolean) a flag used to indicate that a new dataset file should be made. If not present then the existing file will be extended
- p: [optional] (String) the path to the location for creation of the dataset and csp file. If not present, this will be created ```System.getProperty("user.home")``` under the title "CSPMGraphSynthesis"
- d: [optional] (boolean) a flag to indicate whether to include decorations in CSP channels. These are used to pass parameters along the channels
- re: [optional] (boolean) a flag to indicate whether to include renaming of CSP channels
- b: [required] (Integer) the number of basic graphs generate that form a basis of the complex CSP models
- c: [required] (Integer) the number of complex graphs to create based on the basic graphs

  
## Step-by-step Replication
### Dataset creation
- Hosted dataset:
- Parameters used to create a new training dataset.
  ``` -b 50 -c 200 -p [local filepath] -r```  
- To build the dataset, the following parameters were used
  ``` -b 50 -c 200 -p [local filepath]```  
 
### Dataset refinement
Manual refinement was required to balance the generated dataset for use in initial training.  
The generated examples for CSP are weighted heavily towards deadlocked models, resulting in the dataset consisting of more failure cases when verifying against a deadlock-free assertion. 

### Further details
The creation of deadlock-free examples was at a rate of 126 to every 10000 failing cases.  
For every CSP file generated, multiple assertions may be specified. In the generation of this dataset, ~43,000 assertions were defined across ~3650 CSP files for an average of 12 assertions per file.  
Of the ~3680 CSP files generated, ~295 of these had passing assertions; indicating an 8% chance of files including deadlock-free processes. 

To guarantee enough examples to create a balanced dataset of 1000 entries (500 passing:500 failing), approximately 50000 assertions would need creating.  
This is achievable through parameters:   
``` -b 300 -c 4000 ```


## Acknowledgements
This project is an outcome of the AI4Math fund, a program of Renaissance Philanthropy.
     
