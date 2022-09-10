import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalysersOverviewComponent } from './analysers-overview.component';

describe('AnalysersOverviewComponent', () => {
  let component: AnalysersOverviewComponent;
  let fixture: ComponentFixture<AnalysersOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnalysersOverviewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnalysersOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
