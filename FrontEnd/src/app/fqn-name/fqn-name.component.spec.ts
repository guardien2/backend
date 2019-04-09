import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FqnNameComponent } from './fqn-name.component';

describe('FqnNameComponent', () => {
  let component: FqnNameComponent;
  let fixture: ComponentFixture<FqnNameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FqnNameComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FqnNameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
