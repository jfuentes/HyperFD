/* Output from p2c 1.21alpha-07.Dec.93, the Pascal-to-C translator */
/* From input file "thg2.pas" */


#include "p2c.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define maxv            288
#define TRUE 1
#define FALSE 0

typedef short variable_range;

typedef long variable[maxv / 32 + 2];

typedef struct setnode {
  long *vari;
  struct setnode *next;
} setnode;

typedef struct gennode {
  long *vari;
  struct gennode *next, *com_part, *sep_part;
} gennode;

typedef struct memdep {
  gennode *levelgen;
  unsigned long upper_lim;
  struct memdep *next;
} memdep;

typedef struct tokennode {
  variable tokenset;
  unsigned long links;
  struct tokennode *next;
} tokennode;

typedef struct trannode {
  gennode *node;
  tokennode *token;
  struct trannode *next;
} trannode;

typedef struct dsetnode {
  /*This is a doubly-linked list of variables. It is used*/
  long *vari;   /*to store "monogamic",*/
  struct dsetnode *next;   /* the set of clauses which are hit by only */
  struct dsetnode *previous;   /* one node of the current transversal. */
} dsetnode;


typedef struct thunk {
  trannode *tp;
  gennode *trail;
  trannode *af, *sf0, *sf1, *endaf, *endsf0, *endsf1, *endtp;
  unsigned long action, k;
  variable buff;
  unsigned long maxk, limit;
  trannode *piece;
  dsetnode *added_node, *level_mono, *end_level_mono;
  /* Now the typical parameters that are called by name follow */
  trannode *t;
  memdep *depthpoint;
  setnode *cl;
  unsigned long depth;
  variable maintoken;
  /*And the pointers required to form the doubly-linked list */
  struct thunk *next, *previous;
  unsigned long prvpwr;
} thunk;   /*definition of thunk */


static unsigned long count;
static int nohelp, checksim, exinput, exoutput, genoutput, suppressoutput,
	       sortoutput, outputinstance;
static short outputscenario;
static char inname[256], outname[256];
static char sortmode[256];
static unsigned long i, j, k, m, n, c, depth, tcount, fcount, num;
static setnode *instance, *models, *cl;
static dsetnode *monogamic;
static setnode *memstart, *endmemstart, *tempmemstart;
static FILE *inf, *outf;
static variable tv, diaf1, diaf2, omega;
static trannode *tempv, *initt, *initvi, *vvt, *gvv, *free_space;
static tokennode *token_space;
static dsetnode *clause_space;
static unsigned long totaltime;
static int success;
static memdep *mainstruct, *depthpoint;
static unsigned long outstep, viewstep;


static thunk *sspp, *sspp_new, *topstack;
static char inf_NAME[100];
static char outf_NAME[100];

int inset(int e, long *set){
int size=sizeof(set);
int i=0;
while(i<size && e!=set[i]) i++;

if(i!=size) return TRUE;
else return FALSE;
}


static void printhelp()
{
  /*Prints a help message if the -h switch is on */
  printf("\nSwitches for program \n\n");   /*20a*/
  /*20a*/
  /*20a*/
  printf("-h            : this help\n\n");   /*20a*/
  /*20a*/
  printf("-i <filename> : user defined input filename  - default inf.\n");
      /*20a*/
  printf("              : Every hyperedge should be in a different line. \n");
  printf("              : A \"0\" in i-th place denotes that node i is in the\n");
  printf("              : hyperedge, any other symbol denotes that it is not.\n");
  printf("              : Lines beginning with \"#\" are considered comments\n");
  printf("-o <filename> : user defined output filename - default outf\n\n");
      /*20a*/
  /*20a*/
  printf("-c            : check simplicity             - default no\n");
      /*20d*/
  printf("-q            : suppress any output on the screen\n");
  printf("-sp <step>    : outputs a message every <step> transversals\n");
  printf("              : (only when in regular form). Default 10000\n\n");
  /*20a*/
  printf("-s1           : output in generalized form\n");   /*20a*/
  printf("-s2           : output in regular form - default\n");   /*20a*/
  printf("-s3           : output in regular form with sorting. Beware with\n");
  printf("              : this one. Transversals are first stored and then sorted\n");
  printf("              : so memory requirements may be high. Without it every\n");
  printf("              : transversal is output and then forgotten.\n");
  printf("-s4           : just counts the transversals\n");   /*20a*/
}  /*printhelp*/


static void read_switches(int argc, char *argv)
{
  char STR1[256], STR2[256], STR3[256];

  nohelp = TRUE;
  outputscenario = 2;
  exinput = FALSE;   /*if TRUE the input filename is taken externally*/
  exoutput = FALSE;   /*if TRUE the output filename is taken externally*/
  checksim = FALSE;   /*if TRUE checks simplicity*/
  genoutput = FALSE;   /*if FALSE regular output enabled*/
  suppressoutput = FALSE;   /*if TRUE no screen output is produced */
  sortoutput = FALSE;
      /*if TRUE output is sorted before hand, for genoutput=FALSE only*/
  strcpy(sortmode, "no");
  viewstep = 10000;
      /* This is the default step in counting the transversals. */

  if (argc != 1) {
    count = 0;
    while (count != argc - 1) {
      count++;
      if (!strcmp(strcpy(STR1, argv[count]), "-h"))
	nohelp = FALSE;
      /*Output will be in */
      if (!strcmp(strcpy(STR1, argv[count]), "-s1"))   /*generalized form*/
	outputscenario = 1;
      if (!strcmp(strcpy(STR1, argv[count]), "-s2"))   /*regular form*/
	outputscenario = 2;
      if (!strcmp(strcpy(STR1, argv[count]), "-s3"))   /*sorted regular*/
	outputscenario = 3;
      if (!strcmp(strcpy(STR1, argv[count]), "-s4"))   /*just counting it*/
	outputscenario = 4;
      /*Input name*/
      if (!strcmp(strcpy(STR1, argv[count]), "-i")) {
	if (*strcpy(STR2, argv[count+1]) != '\0') {
	  count++;
	  exinput = TRUE;
	  strcpy(inname, argv[count]);
	}
      }
      /*Output name*/
      if (!strcmp(strcpy(STR1, argv[count]), "-o")) {
	if (*strcpy(STR2, argv[count+1]) != '\0') {
	  count++;
	  exoutput = TRUE;
	  strcpy(outname, argv[count]);
	}
      }
      if (!strcmp(strcpy(STR1, argv[count]), "-sp")) {
	if (*strcpy(STR2, argv[count+1]) != '\0') {
	  count++;
	  sscanf(strcpy(STR3, argv[count]), "%ld", &viewstep);
	}
      }
      if (!strcmp(strcpy(STR1, argv[count]), "-c"))
	checksim = TRUE;
      if (!strcmp(strcpy(STR1, argv[count]), "-p"))
	outputinstance = TRUE;
      if (!strcmp(strcpy(STR1, argv[count]), "-q"))
	suppressoutput = TRUE;
      if (strcmp(strcpy(STR1, argv[count]), "-sort"))
	continue;
      if (*strcpy(STR2, argv[count+1]) != '\0') {
	count++;
	strcpy(sortmode, argv[count]);
	if (strcmp(sortmode, "asc") && strcmp(sortmode, "desc") &&
	    strcmp(sortmode, "no"))
	  printf("Invalid sort mode, ignored - enter \"asc\" or \"desc\"\n");
      }
    }  /*20a*/
  }

  switch (outputscenario) {

  case 1:
    genoutput = TRUE;
    break;

  case 2:
    genoutput = FALSE;
    break;

  case 3:
    genoutput = FALSE;
    sortoutput = TRUE;
    break;
  }

  if (suppressoutput)
    return;

  if (exinput)
    printf("Input filename         %s\n", inname);
  if (exoutput)
    printf("Output filename        %s\n", outname);
  if (checksim)
    printf("Input instance will be checked for simplicity\n");
  switch (outputscenario) {

  case 1:
    printf("Output in generalized form\n");
    break;

  case 2:
    printf("Output in regular form -default\n");
    break;

  case 3:
    printf("Output in regular form with sorting\n");
    break;

  case 4:
    printf("Only the number of transversals will be computed\n");
    break;
  }
}  /*read_switches*/


static void printtran(trannode *trans)
{
  trannode *tempt = trans;
  unsigned long i, FORLIM;

  tcount = 1;
  while (tempt != NULL) {
    fcount = 0;
    fprintf(outf, "gn:");
    FORLIM = n;
    for (i = 1; i <= FORLIM; i++) {
      if (inset((int)i, tempt->node->vari)) {
	fprintf(outf, "%3lu,", i);
	fcount++;
      }
    }
    tcount *= fcount;
    tempt = tempt->next;
  }
  num += tcount;
  putc('\n', outf);
}  /*printtran*/


static void print_models(setnode *instance, unsigned long n, unsigned long m)
{
  unsigned long i, j;
  setnode *current = instance;

  for (i = 1; i <= m; i++) {
    for (j = 1; j <= n; j++) {
      if (inset((int)j, current->vari))
	putc('0', outf);
      else
	putc('*', outf);
    }
    putc('\n', outf);
    current = current->next;
  }
}  /* print_models  */


static void read_instance(setnode **instance, unsigned long *n, unsigned long *c)
{
  unsigned long i, j, class_, up, down;
  /*packed array[1..1] of*/
  char buff;
  setnode *temp, *uppoint;
  setnode *a[maxv];
  long SET[11];

  *n = 0;
  *c = 0;
  for (i = 0; i < maxv; i++)
    a[i] = NULL;
  while (!P_eof(inf)) {
    class_ = 0;
    temp = (setnode *)malloc(sizeof(setnode));
    temp->vari = (long *)malloc((maxv / 32 + 2) * sizeof(long));
	/* create a new set for the clause*/
    temp->next = NULL;   /* initially every clause is a */
    P_expset(temp->vari, 0L);   /* single set of the FALSE variables */
    j = 0;
    while (!P_eoln(inf)) {
      buff = getc(inf);
      if (buff == '#') {
	while (!P_eoln(inf))
	  buff = getc(inf);
	fscanf(inf, "%*[^\n]");
	getc(inf);
      } else {   /*or (buff=' 0')*/
	j++;
	if (buff == '0') {
	  P_addset(temp->vari, (int)j);
	  class_++;
	}
      }
    }
    if (*n == 0)
      *n = j;
    else {
      if (*n != j)
	printf("Error. Two lines with different number of nodes\n");
    }
    (*c)++;
    fscanf(inf, "%*[^\n]");
    getc(inf);
    if (strcmp(sortmode, "asc") && strcmp(sortmode, "desc")) {
      temp->next = *instance;
      *instance = temp;
      continue;
    }
    if (!strcmp(sortmode, "desc")) {  /* here starts sorted input desc*/
      if (a[class_-1] != NULL) {
	temp->next = a[class_-1]->next;
	a[class_-1]->next = temp;
	continue;
      }
      down = class_;
      while (a[down-1] == NULL && down != 1)
	down--;
      temp->next = a[down-1];   /* this is always the next node in the list */
      up = class_;
      while (a[up-1] == NULL && up != *n)
	up++;
      if (a[up-1] == NULL)
	*instance = temp;
      else {
	uppoint = a[up-1];
	while (uppoint->next != temp->next)
	  uppoint = uppoint->next;
	uppoint->next = temp;
      }
      a[class_-1] = temp;   /* temp is the first edge with "class" nodes */
      continue;
    }
    if (a[class_-1] != NULL) {
      temp->next = a[class_-1]->next;
      a[class_-1]->next = temp;
      continue;
    }
    up = class_;
    while (a[up-1] == NULL && up != *n)
      up++;
    temp->next = a[up-1];   /* this is always the next node in the list */
    down = class_;
    while (a[down-1] == NULL && down != 1)
      down--;
    if (a[down-1] == NULL)
      *instance = temp;
    else {
      uppoint = a[down-1];
      while (uppoint->next != temp->next)
	uppoint = uppoint->next;
      uppoint->next = temp;
    }
    a[class_-1] = temp;   /* temp is the first edge with "class" nodes */
    /* here ends sorted input desc*/
    /* here starts sorted input asc*/
    /* here ends sorted input asc*/
  }  /* for i:=1 to c */
}  /* read_instance */


static void check_simplicity(setnode *instance)
{
  setnode *ip = instance;
  setnode *jp;
  int error = FALSE;

  while (ip != NULL) {
    jp = ip->next;
    while (jp != NULL) {
      if (P_setequal(jp->vari, ip->vari)) {
	printf("Alg: duplicate clause found\n");
	error = TRUE;
      } else {
	if (P_subset(jp->vari, ip->vari) || P_subset(ip->vari, jp->vari)) {
	  error = TRUE;
	  printf("Alg: containment found\n");
	}
      }
      jp = jp->next;
    }
    ip = ip->next;
  }
  if (!error) {
    if (!suppressoutput)
      printf("Alg: instance was ok\n");
  }
}  /* check_simplicity */


static void lex_sort(setnode **instance)
{
  setnode *temp, *previoustemp, *zerolist, *endzerolist;
  unsigned long j;

  for (j = n; j >= 1; j--) {
    zerolist = NULL;
    previoustemp = NULL;
    temp = *instance;
    while (temp != NULL) {
      if (!inset((int)j, temp->vari)) {
	if (previoustemp != NULL)
	  previoustemp->next = temp;
	if (inset((int)j, (*instance)->vari) && previoustemp == NULL)
	  *instance = temp;
	previoustemp = temp;
	temp = temp->next;
	continue;
      }
      if (zerolist == NULL) {
	zerolist = temp;
	endzerolist = temp;
      } else {
	endzerolist->next = temp;
	endzerolist = temp;
      }
      temp = temp->next;
    }
    if (previoustemp != NULL)
      previoustemp->next = zerolist;
    if (zerolist != NULL)
      endzerolist->next = NULL;
  }  /* for loop */
}  /* lex_sort */


static void assign_new_tvalue(tokennode **ttoken, long *tvalue)
{
  if (token_space == NULL)
    *ttoken = (tokennode *)malloc(sizeof(tokennode));
  else {
    *ttoken = token_space;
    token_space = token_space->next;
  }
  P_setcpy((*ttoken)->tokenset, tvalue);
  (*ttoken)->links = 1;
}  /* assign_new_tvalue */


static void eq_tokens(tokennode **new_token,tokennode ** old_token)
{
  *new_token = *old_token;
  (*old_token)->links++;
}  /*eq_tokens*/



static void mynew(trannode **tp)
{
  /*mastercount:=mastercount+1;*/
  if (free_space == NULL)
    *tp = (trannode *)malloc(sizeof(trannode));
  else {
    *tp = free_space;
    free_space = free_space->next;
  }
}  /* mynew */


static void dispose_transversal(trannode **tp, trannode **endtp)
{
  trannode *temptp;

  temptp = *tp;
  while (temptp != NULL) {
    if (temptp->token->links > 1)
      temptp->token->links--;
    else {
      temptp->token->next = token_space;
      token_space = temptp->token;
    }
    temptp = temptp->next;
  }
  if (*endtp != NULL) {
    (*endtp)->next = free_space;
    free_space = *tp;
  }
}  /* dispose_transversal */


static void expand_and_store(trannode **tp, setnode **models)
{
  short a[maxv][maxv + 1];
  short sel[maxv];
  trannode *tempt;
  unsigned long j, i;
  unsigned long imax = 0;
  unsigned long carry, length;
  setnode *tem;
  int finished = FALSE;
  variable varset;
  unsigned long FORLIM;
  long SET[11];

  tempt = *tp;
  while (tempt != NULL) {
    imax++;
    length = 0;
    FORLIM = n;
    for (j = 1; j <= FORLIM; j++) {
      if (inset((int)j, tempt->node->vari)) {
	length++;
	a[imax-1][length] = j;
      }
    }
    a[imax-1][length+1] = 0;
    a[imax-1][0] = length;
    tempt = tempt->next;
  }  /* end creating the a array for current tempt */
  for (i = 0; i < imax; i++)
    sel[i] = 1;
  while (!finished) {
    P_expset(varset, 0L);
    for (i = 0; i < imax; i++)
      P_addset(varset, a[i][sel[i]]);
    carry = 1;
    for (i = 0; i < imax; i++) {
      sel[i] += carry;
      if (sel[i] > a[i][0]) {
	sel[i] = 1;
	carry = 1;
      } else
	carry = 0;
    }
    if (carry == 1)
      finished = TRUE;
    switch (outputscenario) {

    case 3:  /* must store all transversals to sort them */
      tem = (setnode *)malloc(sizeof(setnode));
      tem->vari = (long *)malloc((maxv / 32 + 2) * sizeof(long));
      P_setcpy(tem->vari, varset);
      tem->next = *models;
      *models = tem;
      break;

    case 2:  /* no sorting, so we output immediately */
      FORLIM = n;
      for (i = 1; i <= FORLIM; i++) {
	if (inset((int)i, varset))
	  putc('0', outf);
	else
	  putc('*', outf);
      }
      putc('\n', outf);
      break;
    }
    outstep++;
    if (outstep == viewstep) {
      m += outstep;
      if (!suppressoutput)
	printf("Found %lu transversals...\n", m);
      outstep = 0;
    }
  }  /* while not finished */
}  /* expand_and_store */


/* Local variables for add_next_clause: */
struct LOC_add_next_clause {
  trannode *tempaf, *tempsf0, *tempsf1, *temptp;
  dsetnode *tempi;

  unsigned long temk;
  int found_tran;

  unsigned long local_action;
  variable newmaintoken;
  unsigned long maxko2;

  /* The declaration of the variables in thunk follows */

  trannode *tp;
  gennode *trail;
  trannode *af, *sf0, *sf1, *endaf, *endsf0, *endsf1, *endtp;
  unsigned long action, k;
  variable buff;
  unsigned long maxk, limit;
  trannode *piece;
  dsetnode *added_node, *level_mono, *end_level_mono;
  variable maintoken;
  memdep *depthpoint;
  setnode *cl;
  unsigned long depth, prvpwr;
} ;


void print_trans(trannode *t, struct LOC_add_next_clause *LINK)
{
  trannode *temptp = t;
  unsigned long ii, FORLIM;

  while (temptp != NULL) {
    FORLIM = n;
    for (ii = 1; ii <= FORLIM; ii++) {
      if (inset((int)ii, temptp->node->vari))
	printf("%3lu", ii);
    }
    printf(" II ");
    temptp = temptp->next;
  }
  putchar('\n');
}

void add_monogamic(setnode **cl, struct LOC_add_next_clause *LINK)
{
  if (clause_space == NULL)
    LINK->added_node = (dsetnode *)malloc(sizeof(dsetnode));
  else {
    LINK->added_node = clause_space;
    clause_space = clause_space->next;
  }
  LINK->added_node->previous = NULL;
  LINK->added_node->vari = (*cl)->vari;
  LINK->added_node->next = monogamic;
  if (monogamic != NULL)
    monogamic->previous = LINK->added_node;
  monogamic = LINK->added_node;
}  /* add_monogamic */

void remove_monogamic(dsetnode **rem_clause, struct LOC_add_next_clause *LINK)
{
  dsetnode *tt;

  tt = (*rem_clause)->next;
  (*rem_clause)->previous->next = (*rem_clause)->next;
      /*there is always a "previous"*/
  if ((*rem_clause)->next != NULL)
    (*rem_clause)->next->previous = (*rem_clause)->previous;
  (*rem_clause)->previous = NULL;
  if (LINK->level_mono != NULL)
    LINK->level_mono->previous = *rem_clause;
  else
    LINK->end_level_mono = *rem_clause;
  (*rem_clause)->next = LINK->level_mono;
  LINK->level_mono = *rem_clause;
  *rem_clause = tt;
}


int generate_next_transversal(struct LOC_add_next_clause *LINK)

{
  /*mem*/
  variable SET, SET1, TEMP;

  LINK->tp = NULL;
  LINK->endtp = NULL;
  LINK->maxko2 = (LINK->limit + 1) >> 1;
      /*this has just a 1 in the first position of limit */
  switch (LINK->action) {

  case 1:
    if (LINK->k > LINK->limit)
      LINK->found_tran = FALSE;
    else {
      LINK->found_tran = TRUE;
      if (LINK->k == LINK->limit) {
	LINK->local_action = 1;   /* only one af node hits cl */
	add_monogamic(&LINK->cl, LINK);
      } else {
	LINK->local_action = 2;
	/* there is also some sf node hitting cl */
      }
    }
    break;

  case 2:
    if (LINK->k > LINK->limit)
      LINK->found_tran = FALSE;
    else {
      LINK->found_tran = TRUE;
      LINK->local_action = 2;
    }
    break;

  case 5:
    if (LINK->k >= LINK->limit) {
      LINK->local_action = 4;   /*requires appropriate node,checking now */
      LINK->found_tran = FALSE;
      while (!LINK->found_tran && LINK->trail != NULL) {
	if (P_setint(SET, LINK->trail->vari, LINK->cl->vari) != 0 &&
	    P_setint(SET1, LINK->trail->vari, LINK->maintoken) == 0) {
	  LINK->found_tran = TRUE;
	  mynew(&LINK->tp);   /* new transversal starts with this node */
	  LINK->tp->next = NULL;
	  LINK->tp->node = LINK->trail;
	  assign_new_tvalue(&LINK->tp->token, LINK->cl->vari);
	  add_monogamic(&LINK->cl, LINK);
	  LINK->endtp = LINK->tp;
	      /* this is also the end of the transversal */
	}
	LINK->trail = LINK->trail->next;
      }
    }  /* if k>=limit */
    else {
      if (LINK->k == ((LINK->prvpwr >> 1) | LINK->maxko2) ||
	  LINK->k == LINK->limit >> 1)
      {   /* no hit from af and only one sf hit */
	LINK->found_tran = TRUE;
	LINK->local_action = 3;   /* means only one sf part hits cl */
	add_monogamic(&LINK->cl, LINK);
	LINK->prvpwr = LINK->k;
      } else {
	LINK->found_tran = TRUE;
	LINK->local_action = 2;
	/* action=5 */
      }
    }
    break;
  }

  if (LINK->found_tran) {
    /*  first make a copy of the af list */
    LINK->tempaf = LINK->af;
    P_expset(LINK->newmaintoken, 0L);
    while (LINK->tempaf != NULL) {
      mynew(&LINK->temptp);
      LINK->temptp->node = LINK->tempaf->node;
      LINK->temptp->next = LINK->tp;
      LINK->tp = LINK->temptp;
      if (LINK->local_action == 4)
	assign_new_tvalue(&LINK->temptp->token, omega);
      else {
	if (LINK->local_action == 1 &&
	    LINK->piece->node->vari == LINK->tp->node->vari) {
	  P_setint(TEMP, LINK->tempaf->token->tokenset, LINK->cl->vari);
	  assign_new_tvalue(&LINK->temptp->token, TEMP);
	} else
	  eq_tokens(&LINK->temptp->token, &LINK->tempaf->token);
      }
      if (LINK->local_action != 2 && LINK->local_action != 4)
	P_setunion(LINK->newmaintoken, LINK->newmaintoken,
		   LINK->temptp->token->tokenset);
      if (LINK->endtp == NULL)
	LINK->endtp = LINK->tp;
      LINK->tempaf = LINK->tempaf->next;
    }
    /*   then a copy of sf0 or sf1 according to the binary representation of k*/
    LINK->temk = LINK->k;
    LINK->tempsf0 = LINK->sf0;
    LINK->tempsf1 = LINK->sf1;
    while (LINK->tempsf0 != NULL) {
      mynew(&LINK->temptp);
      if ((LINK->temk & 1) == 0 && LINK->k < LINK->limit) {
	LINK->temptp->node = LINK->tempsf0->node;
	if (LINK->local_action == 3 && LINK->k == LINK->prvpwr) {
	  P_setint(TEMP, LINK->tempsf0->token->tokenset, LINK->cl->vari);
	  assign_new_tvalue(&LINK->temptp->token, TEMP);
	} else
	  eq_tokens(&LINK->temptp->token, &LINK->tempsf0->token);
	if (LINK->local_action != 2)
	  P_setunion(LINK->newmaintoken, LINK->newmaintoken,
		     LINK->temptp->token->tokenset);
      } else {
	LINK->temptp->node = LINK->tempsf1->node;
	if (LINK->local_action == 4)
	  assign_new_tvalue(&LINK->temptp->token, omega);
	else
	  eq_tokens(&LINK->temptp->token, &LINK->tempsf1->token);
	if (LINK->local_action != 2 && LINK->local_action != 4)
	  P_setunion(LINK->newmaintoken, LINK->newmaintoken,
		     LINK->temptp->token->tokenset);
      }
      LINK->temptp->next = LINK->tp;
      LINK->tp = LINK->temptp;
      if (LINK->endtp == NULL)
	LINK->endtp = LINK->tp;
      LINK->tempsf0 = LINK->tempsf0->next;
      LINK->tempsf1 = LINK->tempsf1->next;
      LINK->temk >>= 1;
    }
  }  /* if found_tran */

  if (LINK->local_action == 2)
    P_setcpy(LINK->newmaintoken, LINK->maintoken);

  if (!(LINK->found_tran && LINK->local_action == 4)) {
	/*update all tokens */
	  return LINK->found_tran;
  }  /* if local_action=4 */
  LINK->tempi = monogamic->next;   /* the first clause is cl, so skip it */
  while (LINK->tempi != NULL) {
    if (P_setint(SET, LINK->tempi->vari, LINK->endtp->node->vari) != 0) {
      remove_monogamic(&LINK->tempi, LINK);
      continue;
    }
    LINK->temptp = LINK->tp;
    while (LINK->temptp != NULL) {
      if (P_setint(SET1, LINK->tempi->vari, LINK->temptp->node->vari) != 0)
	    /*here we don't use the token procedures, since we know what we're doing*/
	      P_setint(LINK->temptp->token->tokenset,
		       LINK->temptp->token->tokenset, LINK->tempi->vari);
      LINK->temptp = LINK->temptp->next;
    }
    LINK->tempi = LINK->tempi->next;
  }  /*while tempi<>nil*/
  LINK->temptp = LINK->tp;
  while (LINK->temptp != NULL) {
    P_setunion(LINK->newmaintoken, LINK->newmaintoken,
	       LINK->temptp->token->tokenset);
    LINK->temptp = LINK->temptp->next;
  }
  return LINK->found_tran;
}  /* generate_next_transversal */

void pop_stack( struct LOC_add_next_clause *LINK)

{
  LINK->af = topstack->af;
  LINK->sf0 = topstack->sf0;
  LINK->sf1 = topstack->sf1;
  LINK->endaf = topstack->endaf;
  LINK->endsf0 = topstack->endsf0;
  LINK->endsf1 = topstack->endsf1;
  LINK->trail = topstack->trail;
  LINK->action = topstack->action;
  LINK->k = topstack->k;
  P_setcpy(LINK->buff, topstack->buff);
  LINK->maxk = topstack->maxk;
  LINK->limit = topstack->limit;
  LINK->prvpwr = topstack->prvpwr;
  LINK->piece = topstack->piece;
  LINK->depthpoint = topstack->depthpoint;
  LINK->cl = topstack->cl;
  LINK->depth = topstack->depth;
  P_setcpy(LINK->maintoken, topstack->maintoken);
  LINK->added_node = topstack->added_node;
  LINK->level_mono = topstack->level_mono;
  LINK->end_level_mono = topstack->end_level_mono;
}  /*pop_stack */

 void push_stack(struct LOC_add_next_clause *LINK)
{
  topstack->trail = LINK->trail;
  topstack->af = LINK->af;
  topstack->sf0 = LINK->sf0;
  topstack->sf1 = LINK->sf1;
  topstack->endaf = LINK->endaf;
  topstack->endsf0 = LINK->endsf0;
  topstack->endsf1 = LINK->endsf1;
  topstack->action = LINK->action;
  topstack->k = LINK->k;
  P_setcpy(topstack->buff, LINK->buff);
  topstack->maxk = LINK->maxk;
  topstack->limit = LINK->limit;
  topstack->prvpwr = LINK->prvpwr;
  topstack->piece = LINK->piece;
  topstack->depthpoint = LINK->depthpoint;
  topstack->cl = LINK->cl;
  topstack->depth = LINK->depth;
  topstack->added_node = LINK->added_node;
  topstack->level_mono = LINK->level_mono;
  topstack->end_level_mono = LINK->end_level_mono;
  if (topstack->next == NULL)
  {  /* have reached end of stack. New node needed */
    sspp_new = (thunk *)malloc(sizeof(thunk));
    sspp_new->previous = topstack;
    sspp_new->next = NULL;
    topstack->next = sspp_new;
    topstack = sspp_new;
  } else
    topstack = topstack->next;
  /* not end of stack. Simply move to next node */
  topstack->t = LINK->tp;
  P_setcpy(topstack->maintoken, LINK->newmaintoken);
  topstack->depthpoint = LINK->depthpoint;
  topstack->cl = LINK->cl->next;
  topstack->depth = LINK->depth;
}  /*push_stack */

int iters=0;

static void add_next_clause()
{
  /*(t:transversal; depthpoint:setlist;
cl:setlist; depth: unsigned)*/
  struct LOC_add_next_clause V;
  trannode *tempt;
  int haschanged, inclu, xena;
  variable buff1;


  setnode *tempclause;
  /* Now the typical parameters that are called by name follow */
  trannode *t;
  variable SET;

_Lentry_point:
iters++;
  /* Pop the typical parameters */
  t = topstack->t;
  P_setcpy(V.maintoken, topstack->maintoken);
  V.depthpoint = topstack->depthpoint;
  V.cl = topstack->cl;
  V.depth = topstack->depth;
  V.depth++;
  /*if ((depth div 1000)*1000=depth) then writeln(depth);*/
  V.k = 0;
  V.level_mono = NULL;
  V.added_node = NULL;
  V.maxk = 0;
  P_setcpy(V.buff, V.cl->vari);
  if (V.depthpoint->upper_lim < V.depth) {
    haschanged = TRUE;
    V.depthpoint = V.depthpoint->next;
  } else
    haschanged = FALSE;
  V.trail = V.depthpoint->levelgen;
  V.action = 5;
  V.af = NULL;
  V.sf0 = NULL;
  V.sf1 = NULL;
  V.endaf = NULL;
  V.endsf0 = NULL;
  V.endsf1 = NULL;
  while (t != NULL) {
    P_setcpy(buff1, t->node->vari);
    if (P_subset(buff1, V.buff))
      inclu = TRUE;
    else
      inclu = FALSE;
    if (P_setint(SET, V.buff, buff1) == 0)
      xena = TRUE;
    else
      xena = FALSE;
    if (inclu || xena) {
      if (inclu) {
	if (V.action == 5) {
	  V.action = 1;
	  V.piece = t;
	} else if (V.action == 1)
	  V.action = 2;
	if (haschanged)
	  t->node = t->node->com_part;
      } else {
	if (haschanged)
	  t->node = t->node->sep_part;
      }
      tempt = t->next;
      t->next = V.af;
      V.af = t;
      t = tempt;
      if (V.endaf == NULL)   /* this marks the end of the af list */
	V.endaf = V.af;
      continue;
    }
    /*if (diaf <> []) and (diaf <> tempt^.vari) then */
    /*  diaf:=tempt^.vari-buff;*/
    V.maxk++;
    mynew(&V.tempsf1);
    V.tempsf1->node = t->node->sep_part;
    eq_tokens(&V.tempsf1->token, &t->token);
    t->node = t->node->com_part;
    tempt = t->next;
    t->next = V.sf0;
    V.sf0 = t;
    t = tempt;
    if (V.endsf0 == NULL) {
      V.endsf0 = V.sf0;   /*this marks the end of the sf0 list*/
      V.endsf1 = V.sf1;   /*this marks the end of the sf1 list */
    }
    V.tempsf1->next = V.sf1;
    V.sf1 = V.tempsf1;
    /*if endsf1=nil then endsf1:=sf1;*/
  }  /* while tempt<> nil */
  V.limit = (1 << V.maxk) - 1;   /* this computes 2**maxk-1 */
  V.prvpwr = V.limit >> 1;

  while (generate_next_transversal(&V)) {   /*mem*/
    /* generate the k-th transversal that is produced from t by adding cl */
    if (V.cl->next == NULL)   /* cl is the last clause to be added */
    {  /* so we add the returned transversals to models */
      if (genoutput)   /*output in generalized form */
	printtran(V.tp);
      else
	expand_and_store(&V.tp, &models);
      /* expand and save the transversal*/
      dispose_transversal(&V.tp, &V.endtp);
    } else {
      push_stack(&V);   /* Instead of recursive call of add_next_clause */
      goto _Lentry_point;
_Lreturn_point: ;
	  /* Here is where we return after finishing add_next_clause */
    }
    /* if cl^.nextclause=nil */
    if (V.added_node != NULL) {
      if (V.added_node->previous != NULL)
	V.added_node->previous->next = V.added_node->next;
      else
	monogamic = V.added_node->next;
      if (V.added_node->next != NULL)
	V.added_node->next->previous = V.added_node->previous;
      V.added_node->next = clause_space;
      clause_space = V.added_node;
      V.added_node = NULL;
    }
    if (V.level_mono != NULL)   /*this level produced former monogamic */
    {  /* clause stored in level_mono list, which we now add */
      V.end_level_mono->next = monogamic;   /*to monogamic */
      if (monogamic != NULL)
	monogamic->previous = V.end_level_mono;
      monogamic = V.level_mono;
      V.level_mono = NULL;
    }
    V.k++;
  }  /* while generate_next_transversal loop */

  /* Now return to free_space, lists af, sf0 and sf1 */
  dispose_transversal(&V.af, &V.endaf);
  dispose_transversal(&V.sf0, &V.endsf0);
  dispose_transversal(&V.sf1, &V.endsf1);
  /* Fix typical parameters. Pop stack and return */
  if (topstack->previous == NULL)
    goto _Lexit_point;
	/* we have returned from the first call, end of proc */
  else {
    topstack = topstack->previous;
    pop_stack(&V);
    goto _Lreturn_point;
  }
  /* if not then pop */
_Lexit_point: ;

}  /* add_next_clause */


static void build_main_data_structure()
{
  memdep *endmainstruct, *tempmainstruct;
  gennode *tempvv, *vv, *gvv;
  int issplit;
  variable diaf1, diaf2, tv;
      /*tv stores the set of known vars up to this point*/
  unsigned long i, FORLIM;
  variable SET;

  cl = instance;
  tempvv = (gennode *)malloc(sizeof(gennode));
  tempvv->vari = (long *)malloc((maxv / 32 + 2) * sizeof(long));
  P_setcpy(tempvv->vari, cl->vari);
  tempvv->next = NULL;
  tempvv->com_part = NULL;
  tempvv->sep_part = NULL;
  vv = tempvv;
  P_setcpy(tv, tempvv->vari);
  mainstruct = (memdep *)malloc(sizeof(memdep));
  mainstruct->levelgen = tempvv;
  mainstruct->upper_lim = 1;
  mainstruct->next = NULL;
  endmainstruct = mainstruct;
  /* this points to the last element of
              mainstruct structure */
  depthpoint = mainstruct;
  FORLIM = c;
  for (i = 2; i <= FORLIM; i++) {
    cl = cl->next;
    issplit = FALSE;
    while (tempvv != NULL) {
      P_setint(diaf1, tempvv->vari, cl->vari);
      P_setdiff(diaf2, tempvv->vari, diaf1);
      if (*diaf1 != 0 && *diaf2 != 0)   /*a node is split*/
	issplit = TRUE;
      tempvv = tempvv->next;
    }  /* while loop */
    if (P_setdiff(SET, cl->vari, tv) != 0)   /*cl has new nodes*/
      issplit = TRUE;
    if (issplit) {
      tempvv = vv;
      vv = NULL;   /* a new vv list will be created, so initialize it */
      while (tempvv != NULL) {
	P_setint(diaf1, tempvv->vari, cl->vari);
	P_setdiff(diaf2, tempvv->vari, diaf1);
	if (*diaf1 != 0 && *diaf2 != 0) {
	      /* this gen variable is split by cl*/
		gvv = (gennode *)malloc(sizeof(gennode));
	      /*for the common part*/
	  gvv->vari = (long *)malloc((maxv / 32 + 2) * sizeof(long));
	  P_setcpy(gvv->vari, diaf1);
	  gvv->next = vv;
	  vv = gvv;
	  tempvv->com_part = gvv;
	  gvv->com_part = NULL;
	  gvv->sep_part = gvv;
	  gvv = (gennode *)malloc(sizeof(gennode));
	      /*for the different part*/
	  gvv->vari = (long *)malloc((maxv / 32 + 2) * sizeof(long));
	  P_setcpy(gvv->vari, diaf2);
	  gvv->next = vv;
	  vv = gvv;
	  tempvv->sep_part = gvv;
	  gvv->com_part = NULL;
	  gvv->sep_part = gvv;
	} else {
	  gvv = (gennode *)malloc(sizeof(gennode));
	  gvv->vari = tempvv->vari;
	  if (*diaf1 != 0) {   /*next clause totally contains tempvv^.vari*/
	    tempvv->com_part = gvv;
	    tempvv->sep_part = NULL;
	  } else {
	    tempvv->com_part = NULL;
	    tempvv->sep_part = gvv;
	  }
	  gvv->next = vv;
	  vv = gvv;
	  gvv->com_part = NULL;
	  gvv->sep_part = gvv;
	}
	/*no split, simply point to the set at previous level*/
	tempvv = tempvv->next;
      }  /* while loop */
      if (P_setdiff(SET, cl->vari, tv) != 0)
      {   /* cl has exclusive variables */
	gvv = (gennode *)malloc(sizeof(gennode));
	gvv->vari = (long *)malloc((maxv / 32 + 2) * sizeof(long));
	P_setdiff(gvv->vari, cl->vari, tv);
	gvv->next = vv;
	vv = gvv;
	gvv->com_part = NULL;
	gvv->sep_part = gvv;
      }  /* since issplit=TRUE, create new node in mainstruct */
      endmainstruct->upper_lim = i - 1;
	  /*final level of validity of this list*/
      tempmainstruct = (memdep *)malloc(sizeof(memdep));
	  /* build new node for next level */
      tempmainstruct->levelgen = vv;
      tempmainstruct->upper_lim = i;   /*first level of validity of new node*/
      tempmainstruct->next = NULL;
      endmainstruct->next = tempmainstruct;
      endmainstruct = tempmainstruct;
      P_setunion(tv, tv, cl->vari);
    }  /* if issplit */
    else
      endmainstruct->upper_lim = i;
    /* current list of gen vars remains valid, so nothing is added*/
    /*just update the level of validity ここでおちる%%%%%%%%%%%%%%%%%%%%%%%%%%*/
    tempvv = vv;   /*be ready for next scan*/
  }  /* for loop */
  tempmainstruct = mainstruct;
}  /* build_main_data_structure*/


main(argc, argv)
int argc;
char *argv[];
{  /* main */
  long SET[11];
  unsigned long FORLIM;

  //PASCAL_MAIN(argc, argv);
  outf = NULL;
  /*20a*/
  /*20a*/
  inf = NULL;
  //read_switches(argc, argv);
  if (!nohelp)
    printhelp();
  else {   /*Default input file name inf*/
    depth = 1;
    if (exinput)
      strcpy(inf_NAME, inname);
    else
      strcpy(inf_NAME, "inf");
    if (exoutput)
      strcpy(outf_NAME, outname);
    else
      strcpy(outf_NAME, "outf");
    if (outf != NULL)
      outf = freopen(outf_NAME, "w", outf);
    else
      outf = fopen(outf_NAME, "w");
    if (outf == NULL)
      //_EscIO2(FileNotFound, outf_NAME);
	printf("FileNot Fpund");
    if (inf != NULL) {
      /*20e*/
      inf = freopen(inf_NAME, "r", inf);
    } else
      inf = fopen(inf_NAME, "r");
    if (inf == NULL)
      _EscIO2("FileNotFound", inf_NAME);
    instance = NULL;
    models = NULL;
    read_instance(&instance, &n, &c);
    if (checksim)
      check_simplicity(instance);

    num = 0;   /*19fe*/
    /*3 counters for procedure printtran*/
    m = 0;
    outstep = 0;
    P_expset(omega, 0L);
    FORLIM = n;
    for (i = 1; i <= FORLIM; i++)
      P_addset(omega, (int)i);
    build_main_data_structure();
    cl = instance;
    free_space = (trannode *)malloc(sizeof(trannode));
    initt = (trannode *)malloc(sizeof(trannode));
	/* This is the first transversal consisting of a single node*/
    initt->node = mainstruct->levelgen;
    cl = instance;
    initt->next = NULL;
    initt->token = (tokennode *)malloc(sizeof(tokennode));
	/* Initialize the token of the first transversal */
    token_space = NULL;
    P_setcpy(initt->token->tokenset, cl->vari);
    clause_space = NULL;
    monogamic = (dsetnode *)malloc(sizeof(dsetnode));
    monogamic->vari = cl->vari;
    monogamic->next = NULL;
    monogamic->previous = NULL;
    fprintf(outf, "# Output graph produced from input in file %s\n", inname);
    fprintf(outf, "# Input graph nodes: %lu\n", n);
    fprintf(outf, "# Input graph hyperedges: %lu\n", c);
    fprintf(outf, "# Output graph hyperedges (below):             \n");
	/* reserve space for the unknown m*/
    sspp = (thunk *)malloc(sizeof(thunk));
    sspp->next = NULL;
    sspp->previous = NULL;
    topstack = sspp;
    topstack->t = initt;
    P_setcpy(topstack->maintoken, initt->token->tokenset);
    topstack->depthpoint = depthpoint;
    topstack->cl = instance->next;
    topstack->depth = depth;
    /* First call of add_next_clause */
    if (c > 1)
	  /*(initt,depthpoint,instance^.nextclause,depth{,totaltime,success)*/
	    add_next_clause();
    else if (genoutput)
      printtran(initt);
    else
      expand_and_store(&initt, &models);
    m += outstep;   /* collect what's left from last call */
    if (genoutput)
      m = num;
    if (!suppressoutput)
      printf("Found %lu transversals\n", m);
    if (!genoutput && sortoutput) {
      lex_sort(&models);
      print_models(models, n, m);
    }
    /*if (not sortoutput) then*/
    /* now we must write m in outf */
    fseek(outf, 0L, 0);
    fprintf(outf, "# Output graph produced from input in file %s\n", inname);
    fprintf(outf, "# Input graph nodes: %lu\n", n);
    fprintf(outf, "# Input graph hyperedges: %lu\n", c);
    fprintf(outf, "# Output graph hyperedges (below): %lu", m);
    if (outf != NULL)
      fclose(outf);
    outf = NULL;
  }
  if (inf != NULL)
    fclose(inf);
  if (outf != NULL)
    fclose(outf);
printf ("#iterations = %d\n", iters);
 // exit(EXIT_SUCCESS);
return 0;
}  /* main */



/* End. */
