---
title: 'Hexatomic: An extensible, OS-independent platform for deep multi-layer linguistic annotation of corpora.'
tags:
  - linguistics
  - linguistic corpora
  - multi-layer annotation
authors:
  - name: Stephan Druskat
    orcid: 0000-0003-4925-7248
    equal-contrib: true
    corresponding: true
    affiliation: "1, 2" # (Multiple affiliations must be quoted)
  - name: Thomas Krause
    orcid: 0000-0003-3731-2422
    equal-contrib: true
    affiliation: 3 # (Multiple affiliations must be quoted)
  - name: Clara Lachenmaier
    orcid: 0000-0002-9207-3420
    affiliation: 2 # (Multiple affiliations must be quoted)
  - name: Bastian Bunzeck
    affiliation: 2
affiliations:
 - name: German Aerospace Center (DLR), Institute for Software Technology, Berlin, Germany
   index: 1
 - name: Friedrich Schiller University Jena, Department of English Studies, Jena, Germany
   index: 2
 - name: Humboldt-Universität zu Berlin,  Department of German Studies and Linguistics, Berlin, Germany
   index: 3
date: 23 August 2022
bibliography: paper.bib
---

# Summary

TODO

This paper contains


- A list of the authors of the software and their affiliations, using the correct format (see the example below).
- A summary describing the high-level functionality and purpose of the software for a diverse, non-specialist audience.
- A Statement of need section that clearly illustrates the research purpose of the software and places it in the context of related work.
- A list of key references, including to other software addressing related needs. Note that the references should include full names of venues, e.g., journals and conferences, not abbreviations only understood in the context of a specific discipline.
- Mention (if applicable) a representative set of past or ongoing research projects using the software and recent scholarly publications enabled by it.
- Acknowledgement of any financial support.

# Statement of need

TODO

# Mathematics

Single dollars ($) are required for inline mathematics e.g. $f(x) = e^{\pi/x}$

Double dollars make self-standing equations:

$$\Theta(x) = \left\{\begin{array}{l}
0\textrm{ if } x < 0\cr
1\textrm{ else}
\end{array}\right.$$

You can also use plain \LaTeX for equations
\begin{equation}\label{eq:fourier}
\hat f(\omega) = \int_{-\infty}^{\infty} f(x) e^{i\omega x} dx
\end{equation}
and refer to \autoref{eq:fourier} from text.

# Citations

Citations to entries in paper.bib should be in
[rMarkdown](http://rmarkdown.rstudio.com/authoring_bibliographies_and_citations.html)
format.

If you want to cite a software repository URL (e.g. something on GitHub without a preferred
citation) then you can do it with the example BibTeX entry below for @fidgit.

For a quick reference, the following citation commands can be used:
- `@author:2001`  ->  "Author et al. (2001)"
- `[@author:2001]` -> "(Author et al., 2001)"
- `[@author1:2001; @author2:2001]` -> "(Author1 et al., 2001; Author2 et al., 2002)"

# Figures

Figures can be included like this:
![Caption for example figure.\label{fig:example}](figure.png)
and referenced from text using \autoref{fig:example}.

Figure sizes can be customized by adding an optional second parameter:
![Caption for example figure.](figure.png){ width=20% }

# Acknowledgements

Hexatomic has been developed in the research project "A minimal infrastructure for the sustainable provision of extensible multi-layer annotation software for linguistic corpora". The project was funded under Deutsche Forschungsgemeinschaft's call "Research Software Sustainability" under grant number 391160252 and ran from October 2018 until December 2021. 

# References