Description of the program:

Multipage: two buttons at bottom of screen, for previous page and next page. 
If no previous page/ next page, the corresponding will be grey, otherwise black. 
Page number displayed at bottom centre. Orientation is disabled. Vertical orientation only. 

Drawing/highlighting : Need to activate by pressing the pen button or the highlighter button.
I recorded paths for each page and drawn with corresponding paint. 
Blue + stroke width 3 for pen and Yellow + stroke width 30 + 70 Alpha for highlighter.

Eraser: Need to activate by clicking the eraser button. 
For eraser feature, I recorded the erasing path and looped through the paths recorded in pen and highlighter.
Then created two regions (full layout size), one with the erasing path and one with each drawn path.
Then used built in quickReject method to check if two regions intersect. 
HOWEVER, I've noticed that (RARELY) straight lines sometimes do not get erased if the user erases too fast. 
So for this reason, please erase slowly and avoid very straight, horizontal lines. ANY OTHER DRAWING WORKS FINE. 

Undo/redo: used undo stack and redo stack for each page to store all actions (no size limit).
Supports drawing and eraser actions. When there's nothing to redo/undo, that button will be grey, otherwise it's purple. 
Note switching page does not count as an action, user cannot redo/undo a page switching. 

Zoom and Pan: Need to activate this by clicking the view button with an eye icon. 
Zoom and Pan is persistent across pages, meaning if user zoomed in on page1 and goes to page 2, page 2 will have the same zoom ratio, same applies for pan. 
Zoom&pan operation looks very choppy but still works. 
Note if user is zoomed out or dragged away from the pdf, user is only allowed to annotate the pdf area, not the blank space outside of the pdf. 
If user wants to annotate the zoomed/panned page, user needs to reactivate the drawing feature via the corresponding button. 
When Zoom/pan is activated, only corresponding gestures will be detected.
When Zoom/pan is deactivated via button, two finger gesture will be treated as one finger, drawing/erasing will be activated and no zoom/pan feature supported.

Data persistency: 
All paths/undo,redo stack are stored separately for each page, so data is persisten across pages. 

Enhancements of the program:

Home button: button on top, left corner which works same as the action bar home button. 

Tool selection: when one tool is selected (view, pen, highlighter, eraser), all other buttons will be grey except for the selected one.

undo/redo: when there's nothing to undo/redo, that button will be grey, otherwise purple.

prev/next page: when there's no previous/next page, that button will be grey, otherwise black.

Reset view: reset view button is on the right of eraser button, it resets the pdf view to original size. 
It is used if user uses zoom/pan and then decides to switch back to the original view. 
This button is never deactivated, even if the page view didn't change, user can still use this button.(nothing happens)

Launch: When app first launches, default tool is view, but no button is deactivated, button gets deactivated once user selects one of the tools.
This is designed so that user know what options they have. 

Information:
Hongjian Zhou
openjdk version "11.0.8" 2020-09-29
Android 11.0 (R)
macOS 10.14.6 (MacBook Pro 2017)

(IntelliJ is way too slow)
IDE: Android Studio 4.1.1
Build #AI-201.8743.12.41.6953283, built on November 4, 2020
Runtime version: 1.8.0_242-release-1644-b3-6915495 x86_64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
macOS 10.14.6
GC: ParNew, ConcurrentMarkSweep
Memory: 1981M
Cores: 8


Image Assets:
Pen image : http://clipart-library.com/clipart/fountain-pen-clipart_2.htm
Highlighter image : http://clipart-library.com/clipart/highlighter-cliparts_4.htm
Eraser image : http://clipart-library.com/clipart/board-eraser-cliparts_3.htm
View button image: built in android image asset
nextPage : built in android image asset
prevPage : built in android image asset 
Undo : built in android image asset
Redo : built in android image asset
PDF : given in started code bundle 
	