import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatTabsModule} from '@angular/material/tabs';
import {HttpClientModule} from '@angular/common/http';
import {NamesComponent } from './names/names.component';

@NgModule({
  declarations: [
    AppComponent,
    NamesComponent

  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatTabsModule,
    HttpClientModule

  ],
  providers: [NamesComponent],
  bootstrap: [AppComponent]
})
export class AppModule { }
