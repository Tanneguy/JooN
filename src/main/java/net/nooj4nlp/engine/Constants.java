package net.nooj4nlp.engine;

import java.awt.Color;

public class Constants
{
	// Name of the Nooj directory which contains resources
	public static final String NOOJ_RESOURCES_DIRECTORY = "ONooJ";

	// Name of the application
	public static final String NOOJ_APPLICATION_NAME = "ONooJ";

	public static final String DIRECTORY_SUFFIX = "_dir";

	// Paths literals
	public static final String LEXICAL_ANALYSIS_PATH = "Lexical Analysis";
	public static final String SYNTACTIC_ANALYSIS_PATH = "Syntactic Analysis";
	public static final String CHAR_VARIANTS_PATH = "charvariants.txt";
	public static final String CHAR_VARIANTS_SUFFIX_PATH = "_charvariants.txt";
	public static final String PROJECTS_PATH = "Projects";

	// Messages
	public static final String EMPTY_FILENAME_MESSAGE = "Filename should not be empty.";
	public static final String CANNOT_OPEN_FILE_MESSAGE = "Cannot open file ";
	public static final String FILE_FORMAT_CONFLICT_ERROR = "NooJ: text file format does not match corpus file format!\n";
	public static final String INCORRECT_FILE_FORMAT_ERROR = "Format is incorrect for file: ";
	public static final String ENTER_CORPUS_FILENAME_MESSAGE = "Enter resulting corpus file name.";
	public static final String ENTER_DIRECTORY_FILENAME_MESSAGE = "Enter a directory where to store the result.";
	public static final String ENTER_PATTERN_MESSAGE = "Enter a pattern to recognize section delimiters.";
	public static final String ENTER_FIRST_FILE_NUMBER_MESSAGE = "Enter the first file's number.";
	public static final String CANNOT_READ_PDF_MESSAGE = "Cannot read PDF file.";
	public static final String CANNOT_READ_FROM_FILE_MESSAGE = "Cannot read from file ";
	public static final String CANNOT_WRITE_TO_FILE_MESSAGE = "Cannot write to file ";
	public static final String CANNOT_LOAD_CHARACTER_VARIANTS_FILE = "Warning: cannot load characters' variants file ";
	public static final String ENCODING_NOT_SUPPORTED_MESSAGE = "Encoding not supported.";
	public static final String CANNOT_HANDLE_ENCODING = "NooJ: Cannot handle encoding #";
	public static final String SUCCESS_CORPUS_MESSAGE = "Success: Corpus ";
	public static final String CONTAINS_MESSAGE = " contains ";
	public static final String FILES_MESSAGE = " files.";
	public static final String SUCCESS_MESSAGE = "Success: ";
	public static final String FILES_CREATED_MESSAGE = " files created in folder: ";
	public static final String CANNOT_SAVE_CORPUS = "Cannot save corpus!";
	public static final String CANNOT_ADD_TEXT_FILE = "Cannot add text file!";
	public static final String CANNOT_FIND_FILE = "Cannot find file ";
	public static final String CANNOT_FIND_GRAMMAR_MESSAGE = "NooJ: Cannot find grammar!";
	public static final String CANNOT_FIND_LEXICAL_RESOURCE = "Cannot find any lexical resource for ";
	public static final String CANNOT_HANDLE_GRAMMAR_MESSAGE = "Problem in grammar ";
	public static final String CANNOT_HANDLE_GRAMMAR_MESSAGE_TITLE = "NooJ: Cannot handle grammar!";
	public static final String CANNOT_LOAD_GRAMMAR_MESSAGE = "Cannot load grammar ";
	public static final String CANNOT_LOAD_GRAMMAR_MESSAGE_TITLE = "NooJ: Problem with grammar!";
	public static final String CANNOT_LOAD_LINGUISTIC_RESOURCE = "NooJ cannot load linguistic resource for ";
	public static final String CANNOT_LOAD_LEXICAL_RESOURCE = "NooJ cannot load lexical resource for ";
	public static final String CANNOT_LOAD_TEXT_MESSAGE = "Cannot load Text ";
	public static final String CANNOT_LOAD_TEXT_MESSAGE_TITLE = "NooJ: No text";
	public static final String CANNOT_IMPORT_TEXT_FILE = "Cannot import text file: ";
	public static final String CANNOT_SPLIT_1 = "NooJ cannot split text file \"";
	public static final String CANNOT_SPLIT_2 = "\" into Text Units.";
	public static final String CANNOT_SPLIT_TEXT_INTO_TU = "Cannot split text into text units!";
	public static final String PERFORM_LING_ANAL_FIRST_MESSAGE = "Please perform linguistic analysis first.";
	public static final String UNKNOWN_DICTIONARY_LARGE_MESSAGE = "Unknown Dictionary is too large (>10MB) to be displayed";
	public static final String UNKNOWN_DICTIONARY_LARGE_CAPTION = "NooJ: dictionary (> 10 MB) is too large";
	public static final String UNKNOWN_DICTIONARY_SAVE_MESSAGE = "Do you want to save it in a file?";
	public static final String UNKNOWN_DICTIONARY_SAVED_MESSAGE = "Unknown Dictionary saved in file ";
	public static final String DICTIONARY_LARGE_MESSAGE = "Dictionary is too large (>10MB) to be displayed";
	public static final String DICTIONARY_LARGE_CAPTION = "NooJ: dictionary (> 10 MB) is too large";
	public static final String DICTIONARY_SAVE_MESSAGE = "Do you want to save it in a file?";
	public static final String DICTIONARY_SAVED_MESSAGE = "Dictionary saved in file ";
	public static final String NOOJ_NUMBER_RANGE_INPUT_MESSAGE = "Input number was outside of range! Please, type a number between 1 and total number of TUs!";
	public static final String NOOJ_TEXT_RANGE_INPUT_MESSAGE = "Input number was outside of range! Please, type a number between 1 and total size of text!";
	public static final String NOOJ_NUMBER_INPUT_MESSAGE = "Please, type a valid number!";
	public static final String SUCCESS_EXPORT_COLORED_TEXTS_MESSAGE = "Success: all colored corpus files have been exported into folder ";
	public static final String ERROR_EXPORT_COLORED_TEXTS_MESSAGE = "Error while exporting all colored corpus files into folder ";
	public static final String SUCCESS_EXPORT_COLORED_TEXT_MESSAGE = "Success: Colored Text saved as ";
	public static final String ERROR_EXPORT_COLORED_TEXT_MESSAGE = "Error while exporting colored text ";
	public static final String ERROR_EXPORT_NONCOLORED_TEXT_MESSAGE = "Error while exporting non-colored text ";
	public static final String FILENAME_PREFIX_WARNING = "WARNING: file name starts with \"_\". Are you sure you want to save it with this prefix?";
	public static final String PROCEED_ANYWAY_MESSAGE = ". Do you want to proceed anyway?";
	public static final String NOOJ_INVALID_OPERATION = "NooJ: invalid operation";

	public static final String NOOJ_INVALID_REGULAR_EXPRESSION = "Invalid regular expression!";
	public static final String NOOJ_LANGUAGE_INCONSISTENCY = "NooJ: Language inconsistency";
	public static final String NOOJ_LANGUAGE_CONFLICT = "Grammar's input language differs from text language. Continue anyway?";

	public static final String DELETE_CORPUS_MESSAGE = "Corpus file already exists. Are you sure you want to delete it?";
	public static final String CANNOT_LOAD_FILE = "Cannot load file ";
	public static final String CORRUPTED_TEXT_FILE = "NooJ: file .jnot corrupted";

	public static final String SAVE_CORPUS_MESSAGE = "Save Corpus?";
	public static final String SAVE_CORPUS_CAPTION_MESSAGE = "NooJ: corpus has not been saved";
	public static final String SAVE_TEXT_MESSAGE = "Save Text?";
	public static final String SAVE_TEXT_CAPTION_MESSAGE = "NooJ: text results have not been saved";
	public static final String CANNOT_SAVE_NEW_CORPUS_MESSAGE = "Error while opening new (empty) corpus.";

	public static final String NOOJ_UPDATE_MESSAGE_TITLE = "NooJ Update";
	public static final String SAVE_FOR_NOOJ = "Save For NooJ";
	public static final String NOOJ_UPDATE_APP_DEFAULT = "Update also NooJ's Application Default?";

	public static final String NOOJ_INTEX_CONVERSION = "Intex => NooJ Conversion";
	public static final String NOOJ_INTEX_CONVERSION_FAILURE = "NooJ: Intex graph => NooJ conversion failure";
	public static final String NOOJ_INTEX_CONVERSION_FLX = "Will convert all C/D/L/R operators (eg: replace all Ls with <B>, delete :s)";
	public static final String NOOJ_INTEX_CONVERSION_MORPH = "Will replace all dots with commas (attention: conversions .=>= and .=>. must be done manually)";
	public static final String NOOJ_INTEX_CONVERSION_SYNTAX = "Will convert symbols <MOT>, <MIN>, <MAJ>, <PRE>, and all dots to commas";
	public static final String CANNOT_IMPORT_GRAPH = "Cannot import graph ";

	public static final String REAPPLY_LINGUISTIC_RESOURCES_MESSAGE = "Please (re)apply linguistic resources!";
	public static final String EDIT_CORPUS_MESSAGE = "You have changed the corpus. Please (re)apply linguistic analysis!";
	public static final String EDIT_CORPUS_CAPTION_MESSAGE = "NooJ: corpus has been edited!";
	public static final String EDIT_TEXT_MESSAGE = "First (re)apply linguistic resources!";
	public static final String EDIT_TEXT_CAPTION_MESSAGE = "NooJ: text has been edited!";
	public static final String NO_TAS_IS_AVAILABLE = "NooJ: no TAS is available for text!";

	public static final String NOOJ_WARNING = "NooJ WARNING: ";
	public static final String NOOJ_CORRUPTED_CORPUS = "Nooj: Corpus file is corrupted";
	public static final String NOOJ_CORRUPTED_TEXT = "Nooj: Text file is corrupted!";
	public static final String NOOJ_APPLY_GRAMMARS_ERROR = "NooJ Apply Grammars Error";
	public static final String NOOJ_TOKENIZER_ERROR = "NooJ Tokenizer Error";
	public static final String NOOJ_SINT_PARSING_ERROR = "NooJ Syntactic Parsing Error";
	public static final String NOOJ_ERROR = "NooJ Error!";
	public static final String NOOJ_CANNOT_CREATE = "NooJ: cannot create file ";
	public static final String NOOJ_NO_MATCH = "NooJ: No match";
	public static final String NOOJ_SYNC_ERROR = "NooJ Sync Error";
	public static final String NOOJ_FILE_ERROR = "NooJ: file error";
	public static final String NOOJ_PROTECTED_RESOURCE = "NooJ: protected resource";

	public static final Object DO_NOT_KNOW_SAVE_MESSAGE = "Do not know how to save current window";
	public static final Object DO_NOT_KNOW_SAVE_AS_MESSAGE = "Do not know how to save-as current window";

	// New/open corpus constants
	public static final String CHARACTERS_LIT = "Characters";
	public static final String TOKENS_LIT = "Tokens";
	public static final String DIGRAMS_LIT = "Digrams";
	public static final String ANNOTATIONS_LIT = "Annotations";
	public static final String UNKNOWNS_LIT = "Unknowns";
	public static final String AMBIGUITIES_LIT = "Ambiguities";
	public static final String UNAMBIGUOUS_WORDS_LIT = "Unambiguous Words";
	public static final int ANNOTATIONS_MAX_ITEMS = 1000;

	// Open text constants
	public static final String CLOSE_CORPUS_MESSAGE = "Please close corpus before quitting";
	public static final String CLOSE_CORPUS_CAPTION = "NooJ: a corpus is opened";
	public static final String CANNOT_SAVE_TEXT_MESSAGE = "Cannot save text";
	public static final String CANNOT_SAVE_TEXT_CAPTION = "NooJ: undefined file name";
	public static final String NO_SELECTED_LEX_RESOURCE1 = "There are no selected lexical resource for ";
	public static final String NO_SELECTED_LEX_RESOURCE2 = ".\nAre you sure you want to continue?";

	// Find/Replace constants
	public static final String FIND_REPLACE_TITLE = "Find/Replace in ";
	public static final String FIND_REPLACE_BEFORE_OPENING_FIND_REPLACE = "Please select a text or dictionary before!";
	public static final String FIND_REPLACE_BEFORE_OPENING_FIND_REPLACE_TITLE = "NooJ: No Text to look in";
	public static final String FIND_REPLACE_FIND_ACTION_ERROR = "No text to work on!";
	public static final String FIND_REPLACE_EMPTY_PATTERN = "Invalid empty pattern!";
	public static final String FIND_REPLACE_EMPTY_PATTERN_TITLE = "NooJ: no pattern to look for";
	public static final String FIND_REPLACE_INVALID_PATTERN = "NooJ: PERL pattern is invalid!";
	public static final String FIND_REPLACE_INVALID_PATTERN_REGULAR = "Invalid pattern!";
	public static final String FIND_REPLACE_PERL_PATTERN_NOT_FOUND = "NooJ: PERL pattern not found!";
	public static final String FIND_REPLACE_PATTERN_NOT_FOUND = "Pattern not found!";
	public static final String FIND_REPLACE_EXACT_PATTERN_NOT_FOUND = "Exact pattern not found!";
	public static final String FIND_REPLACE_NO_MORE_MATCH = "No more match!";
	public static final String FIND_REPLACE_CANNOT_PERFORM_REPLACEMENT = "Cannot perform replacement!";
	public static final String FIND_REPLACE_ENTER_EXACT_PATTERN = "Enter an exact pattern to look for!";
	public static final String FIND_REPLACE_NO_MORE_MATCH_TO_WORK_WITH = "NooJ: No more match to work with!";
	public static final String FIND_REPLACE_PERL_PATTERN_NOT_FOUND_NOTIFICATION = "PERL pattern not found";
	public static final String FIND_REPLACE_QUESTION_OF_WORKING_WITH_NEW_DIC = "Do you want to work with new created dictionary ";
	public static final String FIND_REPLACE_NO_LINE_HAS_BEEN_EXTRACTED = "No line has been extracted!";
	public static final String FIND_REPLACE_NO_LINE_HAS_BEEN_FILTERED = "No line has been filtered out!";
	public static final String FIND_REPLACE_NOOJ_NO_TEXT_TO_LOOK_IN = "NooJ has no text to look in!";
	public static final String FIND_REPLACE_NO_MATCHING_LINE = "No matching line!";
	public static final String FIND_REPLACE_REPLACE_ALL_FROM_FILE_NOTIFY = "Replacement file must contain lines such as:\naaa,bbb\nccc,ddd\netc.";

	// Concordance constants
	public static final String CONCORDANCE_IS_EMPTY = "Concordance is empty!";
	public static final String CONCORDANCE_NOT_SYNC_WITH_TEXT = "Concordance is not synced with text!";
	public static final String EMPTY_ANNOTATIONS = "Annotations are empty!";
	public static final String NOOJ_ANNOTATING_STOPPED = "NooJ stopped annotating the corpus";
	public static final String NOOJ_TEXT_ANNOTATION_SUCCESS = "NooJ: text was annotated";
	public static final String CONFIRM_SAVE_AS = "Confirm Save As";
	public static final String CANNOT_EXPORT_CONCORDANCE_TO_TXT_TITLE = "NooJ: cannot export concordance entries";
	public static final String CANNOT_EXPORT_INDEX_CONCORDANCE_TITLE = "NooJ: cannot export index entries";
	public static final String CANNOT_EXTRACT_TEXT_UNITS = "NooJ: cannot extract matching text units";
	public static final String CANNOT_CREATE_CONCORDANCE_FILE = "Cannot create concordance file ";
	public static final String CANNOT_CREATE_CONCORDANCE_SITE = "Cannot create concordance site ";
	public static final String CANNOT_CREATE_CONCORDANCE_INDEX_FILE = "Cannot create index file ";
	public static final String CANNOT_CREATE_SUBCORPUS = "Cannot create sub-corpus ";
	public static final String NOOJ_EXPORT_CONCORDANCE = "NooJ Export Concordance";
	public static final String NOOJ_EXPORT_INDEX = "NooJ Export Index";
	public static final String NOOJ_EXPORT_MATCHING_TEXT_UNITS = "NooJ Export Matching Text Units";
	public static final String NOOJ_EXPORT_NON_MATCHING_TEXT_UNITS = "NooJ Export Non-Matching Text Units";
	public static final String EXPORT_CONCORDANCE_SUCCESS = "Success: concordance saved as ";
	public static final String EXPORT_INDEX_SUCCESS = "Success: index saved as ";
	public static final String EXTRACT_MATCHING_TEXT_UNITS_SUCCESS = "Success: matching text units' corpus saved as ";
	public static final String EXTRACT_NON_MATCHING_TEXT_UNITS_SUCCESS = "Success: non-matching text units' corpus saved as ";
	public static final String CONCORDANCE_WEB_SITE_NAME = "concord";
	public static final String CANNOT_SAVE_CONCORDANCE = "Cannot save concordance!";
	public static final String CANNOT_LOAD_CONCORDANCE = "Cannot load concordance";
	public static final String SELECT_FOR_CONCORDANCE_MESSAGE = "Please select a text or a corpus for the concordance";
	public static final String SELECT_FOR_CONCORDANCE_CAPTION = "NooJ: needs a text/corpus to link a concordance to";
	public static final String SELECT_TEXT_OR_CORPUS_MESSAGE = "Please Select a text or a corpus";
	public static final String SELECT_TEXT_OR_CORPUS_CAPTION = "NooJ cannot link concordance to text nor corpus";
	public static final String CANNOT_SAVE_CONCORDANCE_FOR_NOOJ = "NooJ: cannot SaveForNooJ a concordance";
	public static final String NOOJ_NOT_IMPLEMENTED = "Not implemented!";

	// Statistics constants
	public static final String FREQUENCIES = "Frequencies";
	public static final String STANDARD_SCORE = "StandardScore";
	public static final String RELEVANCES = "Relevances";
	public static final String SIMILARITY = "Similarity";

	public static final String STATISTICS_CANNOT_CREATE_REPORT = "Cannot create statistical report file ";
	public static final String STATISTICS_CANNOT_SAVE_REPORT_TITLE = "NooJ: cannot save statistical report";
	public static final String STATISTICS_CANNOT_SAVE_RELEVANCE_REPORT = "Cannot create Relevance report file ";
	public static final String STATISTICS_CANNOT_SAVE_RELEVANCE_REPORT_TITLE = "NooJ: cannot save Relevance report";
	public static final String STATISTICS_CANNOT_SAVE_SIMILARITY_REPORT = "Cannot create Similarity report file ";
	public static final String STATISTICS_CANNOT_SAVE_SIMILARITY_REPORT_TITLE = "NooJ: cannot save Similarity report";
	public static final String NOOJ_PROBLEM = "NooJ: Problem";
	public static final String STATISTICS_CORRUPTED_CORPUS = "Corpus is corrupted: cannot read text ";
	public static final String STATISTICS_FREQUENCY_REPORT_SUCCESS = "NooJ: frequency report successfully created!";
	public static final String STATISTICS_FREQUENCY_REPORT_SAVED_TO_A_FILE = "Success: Terms Frequency report saved in file ";
	public static final String STATISTICS_STANDARD_SCORE_REPORT_SUCCESS = "NooJ: standard score report successfully created!";
	public static final String STATISTICS_STANDARD_SCORE_SAVED_TO_A_FILE = "Success: Standard Score report saved in file ";
	public static final String STATISTICS_RELEVANCE_REPORT_SUCCESS = "NooJ: Relevance report successfully created!";
	public static final String STATISTICS_RELEVANCE_SAVED_TO_A_FILE = "Success: Relevance report saved in file ";
	public static final String STATISTICS_SIMILARITY_REPORT_SUCCESS = "NooJ: Similarity report successfully created!";
	public static final String STATISTICS_SIMILARITY_SAVED_TO_A_FILE = "Success: Similarity report saved in file ";

	// Grammar constants
	public static final String GRAMMAR_CANNOT_FIND_MAIN_GRAPH = "Cannot find the main graph!";
	public static final String GRAMMAR_INVALID_LIST_OF_TRANSFORMED_PHRASES = "Invalid list of transformed phrases' constraints";
	public static final String GRAMMAR_CANNOT_LOAD_LINGUISTIC_RESOURCE = "NooJ cannot load linguistic resource";
	public static final String GRAMMAR_COMPILATION_ERROR = "NooJ Grammar Compilation Error";
	public static final String GRAMMAR_CANNOT_READ_PHRASE_TO_PARSE = "Cannot read phrase to parse";
	public static final String GRAMMAR_NO_MAIN_GRAPH = "NooJ: no main graph in grammar";
	public static final String GRAMMAR_SEQUENCE_DOES_NOT_MATCH_GRAMMAR = "Sequence does not match grammar (see Debug)";
	public static final String GRAMMAR_SEQUENCE_DOES_NOT_MATCH_GRAMMAR_CONSTRAINTS = "Sequence does not match grammar constraints\r(use debugger)";
	public static final String GRAMMAR_CANNOT_GENERATE_LANGUAGE = "Cannot generate the language";
	public static final String GRAMMAR_WARNING_CANNOT_LOAD_LINGUISTIC_RESOURCE = "Warning: Cannot load linguistic resources!";

	// Export xml constants
	public static final String ENTER_ANNOT_MESSAGE = "Please enter annotations as XML tags, e.g. <DATE> or <NP>";
	public static final String ENTER_ANNOT_CAPTION = "NooJ: no tag to export";
	public static final String TRANS_ANNOT_MESSAGE = "Special annotation <TRANS> requires a text field, e.g. <TRANS+FR> or <TRANS+PREDICATE>";
	public static final String TRANS_ANNOT_CAPTION = "NooJ: no text field to use for translation";
	public static final String ANNOT_SUCCESS_MESSAGE = "Success: all annotated corpus files have been exported into folder ";
	public static final String EXPORT_SUCCESS_MESSAGE = "Success: text has been exported into file ";
	public static final String FIRST_SAVE_TEXT_MESSAGE = "First Save Text Results";
	public static final String FIRST_SAVE_TEXT_CAPTION = "NooJ: Text Results have not been saved";

	// Project constants
	public static final String SAVE_TEXT_PROJECT_MESSAGE = "Save text ";
	public static final String MISSING_FILE_PROJECT_CAPTION = "NooJ Project is missing one file";
	public static final String PROJECT_NO_DOCUMENTATION_MESSAGE = "Project has no documentation.";
	public static final String PROJECT_NO_NAME_MESSAGE = "This project has no name.";
	public static final String PROJECT_FILE_CORRUPTED = "NooJ: project file corrupted (1)";
	public static final String CANNOT_DELETE_TEMP_DIR_CAPTION = "NooJ: cannot delete temporary directory ";
	public static final String PROJECT_CANNOT_LOAD_LINGUISTIC_MESSAGE = "There are no linguistic resources for language ";
	public static final String PROJECT_CANNOT_LOAD_LEXICAL_MESSAGE = "There are no lexical resources for language ";
	public static final String PROJECT_CANNOT_LOAD_SYNTACTIC_MESSAGE = "There are no syntactic resources for language ";
	public static final String PROJECT_WARNING = "NooJ Project Warning";
	public static final String CANNOT_LOAD_PROJECT = "Cannot load project ";
	public static final String PROJECT_CANNOT_LOAD_DOC = "NooJ: cannot load documentation";
	public static final String PROJECT_CANNOT_RUN = "Cannot run project ";
	public static final String PROJECT_CORRUPTED_FILE = "NooJ: project file corrupted";
	public static final String PROJECT_RESOURCE_FILE = "Resource file ";
	public static final String PROJECT_DOES_NOT_EXIST = " does not exist.";
	public static final String PROJECT_FILE_INVALID = "NooJ: Project file is invalid";
	public static final String PROJECT_CANNOT_UPDATE_PREFERENCES = "Cannot update preferences from project";

	// Multithreading constants
	public static final String ONE_PROCESS_RUNNING_MESSAGE = "One process is already running.";
	public static final String ONE_PROCESS_ONLY_CAPTION = "NooJ: one process only in this version";
	public static final String MONO_THREAD_MESSAGE = "mono-thread";
	public static final String CANNOT_CLOSE_WINDOW_MESSAGE = "Cannot close window: Press 'Cancel'";
	public static final String NOOJ_PROCESS_RUNNING_CAPTION = "NooJ: a process is running";
	public static final String PROCESS_CANCELED_MESSAGE = "Process canceled";

	// Extensions
	public static final String TXT_EXTENSION = "txt";
	public static final String HTM_EXTENSION = "htm";
	public static final String GRF_EXTENSION = "grf";
	public static final String JNOC_EXTENSION = "jnoc";
	public static final String JNOT_EXTENSION = "jnot";
	public static final String JNOD_EXTENSION = "jnod";
	public static final String JNOG_EXTENSION = "nog";
	public static final String JNOF_EXTENSION = "nof";
	public static final String JNOM_EXTENSION = "nom";
	public static final String JNCC_EXTENSION = "jncc";
	public static final String JNOJ_EXTENSION = "jnoj";
	public static final String JNOP_EXTENSION = "jnop";

	// Log messages
	public static final String LOG_LEX_ANALYSIS_FOR_CORPUS = "Lexical analysis for corpus ";
	public static final String LOG_SYN_ANALYSIS_FOR_CORPUS = "Syntactic analysis for corpus ";
	public static final String LOG_CORPUS_FILE = "Corpus file ";
	public static final String LOG_IS_CORRUPTED = " is corrupted.";
	public static final String LOG_PARSING = " > parsing ";

	// Custom Ntext, Corpus and Engine error messages
	public static final String ERROR_MESSAGE_TITLE_NTEXT_LOAD_FOR_CORPUS = "Error while loading text for corpus!";
	public static final String ERROR_MESSAGE_TITLE_NTEXT_LOAD_JUST_BUFFER = "Error while loading text buffer for corpus!";
	public static final String ERROR_MESSAGE_TITLE_NTEXT_SAVE_FOR_CORPUS = "Error while saving text data for corpus!";
	public static final String ERROR_MESSAGE_TITLE_NTEXT_LOAD = "Error while loading Ntext!";

	public static final String ERROR_MESSAGE_TITLE_CORPUS_ADD_TEXT_FILE = "Error while adding text file to corpus!";
	public static final String ERROR_MESSAGE_TITLE_CORPUS_SAVE_IN = "Error while saving corpus to corpus directory!";
	public static final String ERROR_MESSAGE_TITLE_CORPUS_LOAD = "Error while loading corpus from its directory!";

	public static final String ERROR_MESSAGE_TITLE_ENGINE_APPLY_ALL_GRAMMARS = "Error while applying all of grammars!";

	// Custom Other error messages
	public static final String ERROR_MESSAGE_TITLE_GET_FILE_STREAM = "Error while getting file stream! Cannot fill vocabulary!";
	public static final String ERROR_MESSAGE_TITLE_GET_FILE_STREAM_DEFAULT = "Error while getting file stream!";
	public static final String ERROR_MESSAGE_TITLE_INPUT_OUTPUT_ERROR = "Error while getting file stream! Input-output error!";
	public static final String ERROR_MESSAGE_TITLE_EXPORT_CORPUS_ERROR = "Error while exporting corpus to XML!";
	public static final String ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_NO_CLASS = "Error while doing new syntactic parsing (there is no class)!";
	public static final String ERROR_MESSAGE_TITLE_NEW_SYNTACTIC_PARSING_IO = "Error while doing new syntactic parsing (input-output error)!";
	public static final String ERROR_MESSAGE_TITLE_SAVE_PREVIOUS_VERSION = "Error while saving previous version of a text!";
	public static final String ERROR_MESSAGE_TITLE_NO_FILE_PATH = "Save file path does not exists!";
	public static final String ERROR_MESSAGE_TITLE_TEXT_SAVE = "Error while saving text to text directory!";
	public static final String ERROR_MESSAGE_TITLE_DATE_PARSE = "Error while parsing date in sorting corpus function!";
	public static final String ERROR_MESSAGE_TITLE_WINDOW_ON_TOP = "Error while putting window on top of others!";
	public static final String ERROR_MESSAGE_TITLE_UNSUPPORTED_ENCODING = "Error while encoding! Unsupported encoding!";
	public static final String ERROR_MESSAGE_TITLE_CLOSE_READER_FILE = "Error while closing reader file!";
	public static final String ERROR_MESSAGE_TITLE_PROPERTY_VETO = "Error while setting unappropriate property!";
	public static final String ERROR_MESSAGE_TITLE_COMPUTING_DERIVATIONS = "Error while computing derivations!";
	public static final String ERROR_MESSAGE_TITLE_URL_FORMATTING = "Error while getting path through URL!";
	public static final String ERROR_MESSAGE_CANNOT_LOAD_ICONS = "Error while loading image icons!";
	public static final String ERROR_MESSAGE_TITLE_HEADLESS = "No input devices found!";
	public static final String ERROR_MESSAGE_FILE_NOT_FOUND = "File not found: ";

	// Colors
	public static final Color NOOJ_BLUE_BUTTON_COLOR = Color.BLUE;
	public static final Color NOOJ_PRESSED_BLUE_BUTTON_COLOR = new Color(0x000099); // custom dark blue color
	public static final Color NOOJ_RED_BUTTON_COLOR = Color.RED;
	public static final Color NOOJ_PRESSED_RED_BUTTON_COLOR = new Color(0x95000B); // rose wood color
	public static final Color NOOJ_GREEN_BUTTON_COLOR = Color.GREEN;
	public static final Color NOOJ_PRESSED_GREEN_BUTTON_COLOR = new Color(0x32CD32); // lime green color
	public static final Color NOOJ_GRAY_BUTTON_COLOR = Color.GRAY;
	public static final Color NOOJ_PRESSED_GRAY_BUTTON_COLOR = new Color(0x36454F); // char coal gray color
	public static final Color NOOJ_MAGENTA_BUTTON_COLOR = new Color(0xDA70D6);
	public static final Color NOOJ_PRESSED_MAGENTA_BUTTON_COLOR = new Color(0xFF00FF);
}
